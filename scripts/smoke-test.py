#!/usr/bin/env python3
"""Exercise a deployed stack. Never prints the local upload password."""
import base64
import io
import json
from pathlib import Path
import ssl
import tarfile
import urllib.error
import urllib.request
import uuid
import zipfile

root = Path(__file__).resolve().parents[1]
credentials = dict(line.split('=', 1) for line in (root / '.secrets/upload_credentials').read_text().splitlines())
auth = 'Basic ' + base64.b64encode((credentials['username'] + ':' + credentials['password']).encode()).decode()
context = ssl.create_default_context(cafile=str(root / '.secrets/server.crt'))
base = 'https://127.0.0.1'
prefix = 'smoke-' + uuid.uuid4().hex[:12]
test_prefix = prefix


def request(path, data=None, headers=None, authenticated=True):
    headers = dict(headers or {})
    if authenticated:
        headers['Authorization'] = auth
    req = urllib.request.Request(base + path, data=data, headers=headers)
    try:
        with urllib.request.urlopen(req, context=context, timeout=30) as response:
            return response.status, response.read(), response.headers
    except urllib.error.HTTPError as error:
        return error.code, error.read(), error.headers


def upload(name, content, path=None):
    boundary = '----' + uuid.uuid4().hex
    body = b''
    if path is not None:
        body += f'--{boundary}\r\nContent-Disposition: form-data; name="path"\r\n\r\n{path}\r\n'.encode()
    body += f'--{boundary}\r\nContent-Disposition: form-data; name="file"; filename="{name}"\r\nContent-Type: application/octet-stream\r\n\r\n'.encode() + content + f'\r\n--{boundary}--\r\n'.encode()
    return request('/api/uploads', body, {'Content-Type': 'multipart/form-data; boundary=' + boundary})


def archive(name, member, kind='zip'):
    out = io.BytesIO()
    if kind == 'zip':
        with zipfile.ZipFile(out, 'w') as zipped:
            zipped.writestr(name, member)
    else:
        with tarfile.open(fileobj=out, mode='w') as tar:
            info = tarfile.TarInfo(name)
            info.size = len(member)
            tar.addfile(info, io.BytesIO(member))
    return out.getvalue()


assert request('/')[0] == 200
assert request('/api/health', authenticated=False)[0] == 401
assert request('/upload/', authenticated=False)[0] == 401
assert request('/api/health')[0] == 200
assert request('/upload/')[0] == 200
print('PASS homepage, authenticated upload page and API')
content = b'upload-download-integrity\n'
name = prefix + '.txt'
status, body, _ = upload(name, content)
assert status == 200 and json.loads(body)['code'] == 200, (status, body)
status, body, headers = request('/files/' + name, authenticated=False)
assert status == 200 and body == content
assert headers['Content-Disposition'] == 'inline'
assert headers.get_content_type() == 'text/plain'
assert 'sandbox allow-downloads;' in headers['Content-Security-Policy']
assert 'sandbox allow-downloads;' in request('/files/')[2]['Content-Security-Policy']
assert upload(name, b'overwrite')[0] == 400
assert request('/files/' + name)[1] == content
# Check serving policy and range support independently of browser codec support.
for extension, mime in [('pdf', 'application/pdf'), ('mp3', 'audio/mpeg'), ('mp4', 'video/mp4')]:
    preview_name = test_prefix + '-headers.' + extension
    assert upload(preview_name, content)[0] == 200
    _, _, preview_headers = request('/files/' + preview_name, authenticated=False)
    assert preview_headers['Content-Disposition'] == 'inline'
    assert preview_headers.get_content_type() == mime
    range_status, range_body, range_headers = request('/files/' + preview_name, headers={'Range': 'bytes=0-15'}, authenticated=False)
    assert range_status == 206 and range_body == content[:16]
    assert range_headers['Content-Range'].startswith('bytes 0-15/')
for extension in ('html', 'svg', 'bin'):
    download_name = test_prefix + '-download.' + extension
    assert upload(download_name, content)[0] == 200
    _, _, download_headers = request('/files/' + download_name, authenticated=False)
    assert download_headers['Content-Disposition'] == 'attachment'
    assert 'sandbox allow-downloads;' in download_headers['Content-Security-Policy']
print('PASS streaming upload, TXT preview, protected downloads, overwrite prevention')
for extension in ('zip', 'tar'):
    status, body, _ = upload(prefix + '.' + extension, archive('nested/a.txt', content, extension))
    assert status == 200, (status, body)
    assert request('/files/' + prefix + '/nested/a.txt')[1] == content
    # ZIP and TAR share the same extraction basename, use another prefix for TAR.
    prefix += '-next'
print('PASS ZIP and TAR extraction')
for malicious in ('../escape.txt', '/absolute.txt', '.hidden'):
    status, body, _ = upload(prefix + '-bad.zip', archive(malicious, content))
    assert status == 400, (status, body)
# Reject symlinks in both supported archive formats.
out = io.BytesIO()
with zipfile.ZipFile(out, 'w') as zipped:
    link = zipfile.ZipInfo('link')
    link.create_system = 3
    link.external_attr = 0o120777 << 16
    zipped.writestr(link, '../outside')
assert upload(prefix + '-link.zip', out.getvalue())[0] == 400
out = io.BytesIO()
with tarfile.open(fileobj=out, mode='w') as tar:
    link = tarfile.TarInfo('link')
    link.type = tarfile.SYMTYPE
    link.linkname = '../outside'
    tar.addfile(link)
assert upload(prefix + '-link.tar', out.getvalue())[0] == 400
# A small compressed payload must not bypass the expanded-size quota.
out = io.BytesIO()
with zipfile.ZipFile(out, 'w', compression=zipfile.ZIP_DEFLATED) as zipped:
    with zipped.open('large.txt', 'w') as member:
        block = b'0' * (1024 * 1024)
        for _ in range(257):
            member.write(block)
assert upload(prefix + '-bomb.zip', out.getvalue())[0] == 400
assert upload('../escape.txt', content)[0] == 400
assert upload(prefix + '-safe.txt', content, '../outside')[0] == 400
assert upload(prefix + '-empty.txt', b'')[0] == 400
assert request('/files/.tmp/', authenticated=False)[0] == 403
assert request('/files/' + prefix + '-bad.zip')[0] == 404
assert request('/api/uploads', b'', {})[0] == 400
print('PASS traversal, archive symlinks, decompression quota, hidden files, empty files and missing multipart rejection')
# Remove only this run's random fixture prefix; uploaded user data is untouched.
import subprocess
subprocess.run(['docker', 'compose', 'exec', '-T', 'api_server', 'sh', '-c',
    'for path in /data/uploads/"$1"*; do [ ! -e "$path" ] || rm -rf -- "$path"; done',
    'sh', test_prefix], cwd=root, check=True)
print('All deployment checks passed; this run’s temporary fixtures were removed.')
