import { render, screen } from '@testing-library/react';
import App from './App';
test('shows file browser and uploader', () => {
  render(<App />);
  expect(screen.getByText('File Browser')).toBeInTheDocument();
  expect(screen.getByText('File Uploader')).toBeInTheDocument();
  expect(screen.queryByText('Jupyter Lab')).not.toBeInTheDocument();
});
