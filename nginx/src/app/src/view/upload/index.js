import { Component } from "react";
import { InboxOutlined } from '@ant-design/icons';
import { message, Upload } from 'antd';
import './index.css';
import { Content } from "antd/es/layout/layout";

class FileUpload extends Component {
    render() {
        const { Dragger } = Upload;
        const props = {
            name: 'file',
            multiple: true,
            action: process.env.REACT_APP_BASE_API + '/api/uploads',
            onChange(info) {
                const { status } = info.file;
                if (status !== 'uploading') {
                    console.log(info.file, info.fileList);
                }
                if (status === 'done') {
                    var code = info.file.response?.code
                    if (code !== 200) {
                        message.error(`${info.file.name} 文件上传失败., ${code}`);
                    } else {
                        message.success(`${info.file.name} 文件上传成功.`);
                    }
                } else if (status === 'error') {
                    if (info.file.response === undefined) {
                        message.error(`${info.file.name} 文件上传失败, 服务器未响应`);
                    } else {
                        message.error(`${info.file.name} 文件上传失败.`);
                    }
                    info.file.response = "上传失败"
                }
            },
            onDrop(e) {
                console.log('Dropped files', e.dataTransfer.files);
            },
        };

        var uploadPath = process.env.PUBLIC_URL + '/files/win-ftp/test/tmp/';
        return (
            <Content className="upload-content" di>
                <p>upload to <a href={uploadPath}>{uploadPath}</a></p>
                <Dragger {...props} className="upload-dragger">
                    <p className="ant-upload-drag-icon">
                        <InboxOutlined />
                    </p>
                    <p className="ant-upload-text">点击或拖拽文件到此处上传</p>
                    <p className="ant-upload-hint">
                        支持单个和多个文件同时上传
                    </p>
                </Dragger>
            </Content>
        )
    }
}

export default FileUpload;