import request from '@/utils/request'

// 合并文件
export function fileMerge(param) {
  return request({
    url: '/system/file/merge',
    //前端不校验重复提交
    headers: {
      repeatSubmit: false
    },
    method: 'post',
    //因上传文件较大时，文件合并时也需要更多的时间，所以自定义超时时间
    timeout: 180000,
    params: param
  })
}
