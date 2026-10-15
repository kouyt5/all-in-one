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
        <img src={'' + '/avtor.jpg'} className="App-logo" alt="logo" />
      </header>

      <Content className='App-content'>
        {import.meta.env.VITE_ENABLE_CODE_SERVER === 'true' && (
          <Card hoverable className='App-link' onClick={() => window.location.href = '/code/'}>
            <Meta title="Code Server" description="在线开发环境" />
          </Card>
        )}
        <Card
          hoverable
          className='App-link'
          cover={<img alt="example" src={'' + '/file.svg'} className='Item-Img' />}
          onClick={() =>
            window.location.href = '' + "/files/"
          }
        >
          <Meta title="File Browser" description="文件浏览服务器, 用于共享文件下载" size='small' />
        </Card>

        <Card
          hoverable
          className='App-link'
          cover={<img alt="example" src={'' + '/upload.svg'} className='Item-Img' />}
          onClick={() =>
            window.location.href = "/upload/#/file-upload/"
          }
        >
          <Meta title="File Uploader" description="用于上传共享文件" size='small' />
        </Card>
      </Content>

      <Footer className='App-footer'>
        <span>CC · 文件共享</span>
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