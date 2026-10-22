import { render, screen, within } from '@testing-library/react';
import { afterEach, vi } from 'vitest';
import App from './App';

afterEach(() => vi.unstubAllEnvs());

test('homepage links to file tools and code-server with their icons', () => {
  vi.stubEnv('VITE_ENABLE_CODE_SERVER', 'true');
  render(<App />);
  expect(screen.getByRole('link', { name: '打开 Code Server' })).toHaveAttribute('href', '/code/');
  expect(within(screen.getByRole('main')).getAllByRole('link')).toHaveLength(3);
  expect(screen.getByText('kouyt5的个人小站')).toBeInTheDocument();
  expect(screen.getByRole('img', { name: 'Code Server 图标' })).toHaveAttribute('src', '/code-server.png');
  expect(screen.getByRole('link', { name: '打开 File Browser' })).toHaveAttribute('href', '/files/');
  expect(screen.getByRole('link', { name: '打开 File Uploader' })).toHaveAttribute('href', '/upload/#/file-upload/');
  expect(screen.getByRole('img', { name: 'File Browser 图标' })).toHaveAttribute('src', '/file.svg');
  expect(screen.getByRole('img', { name: 'File Uploader 图标' })).toHaveAttribute('src', '/upload.svg');
  expect(screen.queryByText('Jupyter Lab')).not.toBeInTheDocument();
});

test('disabled code-server is not advertised', () => {
  vi.stubEnv('VITE_ENABLE_CODE_SERVER', 'false');
  render(<App />);
  expect(screen.queryByRole('link', { name: '打开 Code Server' })).not.toBeInTheDocument();
  expect(within(screen.getByRole('main')).getAllByRole('link')).toHaveLength(2);
});

test('footer includes the configured ICP filing link', () => {
  render(<App />);
  const filing = screen.getByRole('link', { name: '蜀ICP备202606064116号' });
  expect(filing).toHaveAttribute('href', 'https://beian.miit.gov.cn/');
  expect(filing).toHaveAttribute('target', '_blank');
  expect(filing).toHaveAttribute('rel', 'noopener noreferrer');
});
