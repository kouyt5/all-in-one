import Loadable from 'react-loadable';


function loading() {
    return (
        <div>加载中...</div>
    );
}

const FileUpload = Loadable ({
    loader: () => import('./upload'),
    loading: loading
});

export {
    FileUpload
}