import { render, screen } from '@testing-library/react';
import { afterEach, vi } from 'vitest';
import App from './App';

afterEach(() => vi.unstubAllEnvs());

test('homepage only links to code-server and includes its icon', () => {
  vi.stubEnv('VITE_ENABLE_CODE_SERVER', 'true');
  render(<App />);
  expect(screen.getByRole('link', { name: '打开 Code Server' })).toHaveAttribute('href', '/code/');
  expect(screen.getAllByRole('link')).toHaveLength(1);
  expect(screen.getByRole('img', { name: 'Code Server 图标' })).toHaveAttribute('src', '/code-server.png');
  expect(screen.queryByText('File Browser')).not.toBeInTheDocument();
  expect(screen.queryByText('File Uploader')).not.toBeInTheDocument();
  expect(screen.queryByText('Jupyter Lab')).not.toBeInTheDocument();
});

test('disabled code-server is not advertised', () => {
  vi.stubEnv('VITE_ENABLE_CODE_SERVER', 'false');
  render(<App />);
  expect(screen.queryByRole('link')).not.toBeInTheDocument();
});
