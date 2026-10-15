import React, { lazy, Suspense } from 'react';
const Upload = lazy(() => import('./upload'));
export function FileUpload() {
  return <Suspense fallback={<div>加载中...</div>}><Upload /></Suspense>;
}
