import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
export default defineConfig({
  plugins: [react()],
  build: { outDir: 'build' },
  test: { environment: 'jsdom', globals: true, setupFiles: './src/setupTests.js', maxWorkers: 1 },
  server: { proxy: { '/api': 'http://127.0.0.1:8080' } }
});
