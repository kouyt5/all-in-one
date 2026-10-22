import { Card } from 'antd';
import './App.css';
import { Content, Footer } from 'antd/es/layout/layout';
import React, { Component } from 'react'
import {
  Routes,
  Route,
  HashRouter as Router,
} from "react-router-dom";
import { FileUpload } from './view'

const { Meta } = Card;

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <img src="/avtor.jpg" className="App-logo" alt="logo" />
      </header>

      <Content className='App-content'>
        <a href="/files/" aria-label="打开 File Browser">
          <Card hoverable className='App-link'
            cover={<img alt="File Browser 图标" src="/file.svg" className='Item-Img' />}>
            <Meta title="File Browser" description="文件浏览与下载" />
          </Card>
        </a>
        <a href="/upload/#/file-upload/" aria-label="打开 File Uploader">
          <Card hoverable className='App-link'
            cover={<img alt="File Uploader 图标" src="/upload.svg" className='Item-Img' />}>
            <Meta title="File Uploader" description="上传共享文件" />
          </Card>
        </a>
        {import.meta.env.VITE_ENABLE_CODE_SERVER === 'true' && (
          <a href="/code/" aria-label="打开 Code Server">
            <Card
              hoverable
              className='App-link'
              cover={<img alt="Code Server 图标" src="/code-server.png" className='Item-Img' />}
            >
              <Meta title="Code Server" description="在线开发环境" />
            </Card>
          </a>
        )}
      </Content>

      <Footer className='App-footer'>
        <span>kouyt5的个人小站</span>
        <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">
          ICP备案号：2026064116
        </a>
      </Footer>
    </div>
  );
}

export default class AppWrapper extends Component {
  render() {
    return (
      // <React.StrictMode>
      <Router>
        {/* <AppRouter /> */}
        <Routes>
          <Route path='/' element={<App />} />
          <Route path='file-upload' element={<FileUpload />} />
        </Routes>
      </Router>
      // </React.StrictMode>
    );
  }
};

// export default AppWrapper;