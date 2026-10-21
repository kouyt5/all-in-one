import { render, screen, within } from '@testing-library/react';
import { afterEach, vi } from 'vitest';
import App from './App';

afterEach(() => vi.unstubAllEnvs());

test('homepage only links to code-server and includes its icon', () => {
  vi.stubEnv('VITE_ENABLE_CODE_SERVER', 'true');
  render(<App />);
  expect(screen.getByRole('link', { name: '打开 Code Server' })).toHaveAttribute('href', '/code/');
  expect(within(screen.getByRole('main')).getAllByRole('link')).toHaveLength(1);
  expect(screen.getByText('kouyt5的个人小站')).toBeInTheDocument();
  expect(screen.getByRole('img', { name: 'Code Server 图标' })).toHaveAttribute('src', '/code-server.png');
  expect(screen.queryByText('File Browser')).not.toBeInTheDocument();
  expect(screen.queryByText('File Uploader')).not.toBeInTheDocument();
  expect(screen.queryByText('Jupyter Lab')).not.toBeInTheDocument();
});

test('disabled code-server is not advertised', () => {
  vi.stubEnv('VITE_ENABLE_CODE_SERVER', 'false');
  render(<App />);
  expect(screen.queryByRole('link', { name: '打开 Code Server' })).not.toBeInTheDocument();
});

test('footer includes the placeholder ICP filing link', () => {
  render(<App />);
  const filing = screen.getByRole('link', { name: 'ICP备案号：xxx' });
  expect(filing).toHaveAttribute('href', 'https://beian.miit.gov.cn/');
  expect(filing).toHaveAttribute('target', '_blank');
  expect(filing).toHaveAttribute('rel', 'noopener noreferrer');
});
