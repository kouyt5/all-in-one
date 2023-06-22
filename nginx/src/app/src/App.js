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
        <img src={process.env.PUBLIC_URL + '/avtor.jpg'} className="App-logo" alt="logo" />
      </header>

      <Content className='App-content'>
        <Card
          hoverable
          className='App-link'
          cover={<img alt="example" src={process.env.PUBLIC_URL + '/code-server.png'} className='Item-Img' />}
          onClick={() =>
            window.location.href = process.env.PUBLIC_URL + "/code/"
          }
        >
          <Meta title="Code Server" description="远程vscode，用于记录日常code" size='small' />
        </Card>

        <Card
          hoverable
          className='App-link'
          cover={<img alt="example" src={process.env.PUBLIC_URL + '/jupyter-lab.svg'} className='Item-Img' />}
          onClick={() =>
            window.location.href = process.env.PUBLIC_URL + "/jupyter/"
          }
        >
          <Meta title="Jupyter Lab" description="jupyter lab，用于科学计算，绘制流程图等" size='small' />
        </Card>

        <Card
          hoverable
          className='App-link'
          cover={<img alt="example" src={process.env.PUBLIC_URL + '/file.svg'} className='Item-Img' />}
          onClick={() =>
            window.location.href = process.env.PUBLIC_URL + "/files/"
          }
        >
          <Meta title="File Browser" description="文件浏览服务器, 用于共享文件下载" size='small' />
        </Card>

        <Card
          hoverable
          className='App-link'
          cover={<img alt="example" src={process.env.PUBLIC_URL + '/upload.svg'} className='Item-Img' />}
          onClick={() =>
            window.location.href = process.env.PUBLIC_URL + "/#/file-upload/"
          }
        >
          <Meta title="File Uploader" description="用于上传共享文件" size='small' />
        </Card>
      </Content>

      <Footer className='App-footer'>
        <a href="http://beian.miit.gov.cn/">蜀ICP备2021xxxxx号</a>
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