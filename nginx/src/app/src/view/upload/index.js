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
            action: 'https://www.mocky.io/v2/5cc8019d300000980a055e76',
            onChange(info) {
                const { status } = info.file;
                if (status !== 'uploading') {
                    console.log(info.file, info.fileList);
                }
                if (status === 'done') {
                    message.success(`${info.file.name} file uploaded successfully.`);
                } else if (status === 'error') {
                    message.error(`${info.file.name} file upload failed.`);
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