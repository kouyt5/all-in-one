#!/usr/bin/env python3
"""Create local credentials and an IP certificate without printing secrets."""
import argparse
import os
from pathlib import Path
import secrets
import subprocess

parser = argparse.ArgumentParser()
parser.add_argument('--ip', required=True)
args = parser.parse_args()
import ipaddress
ipaddress.ip_address(args.ip)
root = Path(__file__).resolve().parents[1]
secret_dir = root / '.secrets'
secret_dir.mkdir(mode=0o700, exist_ok=True)
os.chmod(secret_dir, 0o700)
if not (secret_dir / 'upload.htpasswd').exists():
    password = secrets.token_urlsafe(24)
    hashed = subprocess.check_output(['openssl', 'passwd', '-6', '-stdin'], input=password.encode()).decode().strip()
    (secret_dir / 'upload.htpasswd').write_text('admin:' + hashed + '\n')
    (secret_dir / 'upload_credentials').write_text('username=admin\npassword=' + password + '\n')
if not (secret_dir / 'code_password').exists():
    (secret_dir / 'code_password').write_text(secrets.token_urlsafe(24) + '\n')
if not (secret_dir / 'server.key').exists() or not (secret_dir / 'server.crt').exists():
    subprocess.run(['openssl', 'req', '-x509', '-newkey', 'rsa:3072', '-sha256', '-days', '365', '-nodes',
                    '-keyout', str(secret_dir / 'server.key'), '-out', str(secret_dir / 'server.crt'),
                    '-subj', '/CN=' + args.ip, '-addext', f'subjectAltName=IP:{args.ip},IP:127.0.0.1,DNS:localhost'], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
for path in secret_dir.iterdir():
    os.chmod(path, 0o644 if path.name in ('upload.htpasswd', 'server.crt') else 0o600)
print('Local credentials and certificate ready in .secrets/ (existing files preserved).')
