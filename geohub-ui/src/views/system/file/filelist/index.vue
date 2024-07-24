<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" label-width="68px">
      <el-form-item>
        <el-button type="primary" icon="el-icon-upload" size="mini" @click="upload" v-hasPermi="['system:file:upload']">上传</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:filelist:remove']"
        >删除</el-button>
      </el-col>
<!--      <el-col :span="1.5">
        <el-button
          type="warning"
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['background:filelist:export']"
        >导出</el-button>
      </el-col>-->
    </el-row>

    <el-table :data="filelistList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="文件名" align="center" prop="filename" />
      <el-table-column label="本地地址" align="center" prop="location" />
      <el-table-column label="文件总大小" align="center" prop="totalSize" :formatter="storageFormatter"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleDownload(scope.row)"

          >下载</el-button>
        </template>
        <!--        v-hasPermi="['system:filelist:download']"-->
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改已上传文件列表对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="文件名" prop="filename">
          <el-input v-model="form.filename" placeholder="请输入文件名" />
        </el-form-item>
        <el-form-item label="唯一标识,MD5" prop="identifier">
          <el-input v-model="form.identifier" placeholder="请输入唯一标识,MD5" />
        </el-form-item>
        <el-form-item label="链接" prop="url">
          <el-input v-model="form.url" placeholder="请输入链接" />
        </el-form-item>
        <el-form-item label="本地地址" prop="location">
          <el-input v-model="form.location" placeholder="请输入本地地址" />
        </el-form-item>
        <el-form-item label="文件总大小" prop="totalSize">
          <el-input v-model="form.totalSize" placeholder="请输入文件总大小" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>

  <!-- 上传 -->
  <uploader
      ref="uploader"
      :options="options"
      :autoStart="false"
      @file-added="onFileAdded"
      @file-success="onFileSuccess"
      @file-progress="onFileProgress"
      @file-error="onFileError"
      class="uploader-app">
    <uploader-unsupport></uploader-unsupport>
    <uploader-btn id="global-uploader-btn" :attrs="attrs" ref="uploadBtn" v-show="false">选择文件</uploader-btn>
  </uploader>
</template>

<script setup>
import {fileMerge} from "@/api/system/file/fileuploader/fileuploader.js";
import {ACCEPT_CONFIG} from "@/assets/js/config.js";
import SparkMD5 from "spark-md5";
import { ref, reactive, onMounted } from 'vue';
import { listFilelist, delFilelist, addFilelist, updateFilelist, exportFilelist } from "@/api/system/file/filelist";
import Bus from '../../../../assets/js/bus'
import { getToken } from '@/utils/auth';

const total = ref(0);
const filelistList = ref([]);
const open = ref(false);
const title = ref('');
const ids = ref([])
const multiple = ref(false)
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  filename: undefined,
  identifier: undefined,
  url: undefined,
  location: undefined,
  totalSize: undefined
});

const form = reactive({
  id: undefined,
  filename: undefined,
  identifier: undefined,
  url: undefined,
  location: undefined,
  totalSize: undefined
});

const rules = {
  filename: [{ required: true, message: '文件名不能为空', trigger: 'blur' }],
  identifier: [{ required: true, message: '唯一标识,MD5不能为空', trigger: 'blur' }],
  url: [{ required: true, message: '链接不能为空', trigger: 'blur' }]
};

const baseApi = import.meta.env.VITE_APP_BASE_API;

const options = ref({
  target: baseApi + '/system/file/upload',
  chunkSize: 5 * 1024 * 1000,
  fileParameterName: 'file',
  maxChunkRetries: 2,
  testChunks: true,
  headers: {
    Authorization: ''
  },
  query: () => {}
});

const attrs = ref({
  accept: ACCEPT_CONFIG.getAll()
});

const panelShow = ref(false);

const uploadBtn = ref(null);
const onFileAdded = (file) => {
  panelShow.value = true;
  computeMD5(file);
  Bus.emit('fileAdded');
};

//上传过程中，会不断触发file-progress上传进度的回调
const onFileProgress = (rootFile, file, chunk) => {
  console.log(`上传中 ${file.name}，chunk：${chunk.startByte / 1024 / 1024} ~ ${chunk.endByte / 1024 / 1024}`);
};

const onFileSuccess = (rootFile, file, response, chunk) => {
  let res = JSON.parse(response);
  if (res.needMerge) {
    let param = {
      'filename': rootFile.name,
      'identifier': rootFile.uniqueIdentifier,
      'totalSize': rootFile.size
    }
    fileMerge(param).then(res => {
      // 文件合并成功
      Bus.emit('fileSuccess');
    }).catch(e => {
      console.log("合并异常,重新发起请求,文件名为:", file.name)
      //由于网络或服务器原因,导致合并过程中断线,此时如果不重新发起请求,就会进入失败的状态,导致该文件无法重试
      file.retry();
    });
    // 不需要合并
  } else {
    Bus.emit('fileSuccess');
    console.log('上传成功');
  }
};


function onFileError(rootFile, file, response, chunk) {
  console.log('上传失败')
}

/**
 * 计算md5，实现断点续传及秒传
 * @param file
 */
function computeMD5(file) {
  let fileReader = new FileReader();
  let time = new Date().getTime();
  let blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice;
  let currentChunk = 0;
  const chunkSize = 10 * 1024 * 1000;
  let chunks = Math.ceil(file.size / chunkSize);
  let spark = new SparkMD5.ArrayBuffer();

  file.pause();

  loadNext();

  fileReader.onload = (e => {

    spark.append(e.target.result);

    if (currentChunk < chunks) {
      currentChunk++;
      loadNext();
    } else {
      let md5 = spark.end();
      computeMD5Success(md5, file);
      console.log(`MD5计算完毕：${file.name} \nMD5：${md5} \n分片：${chunks} 大小:${file.size} 用时：${new Date().getTime() - time} ms`);
    }
  });

  fileReader.onerror = function () {
    this.error(`文件${file.name}读取出错，请检查该文件`)
    file.cancel();
  };

  function loadNext() {
    let start = currentChunk * chunkSize;
    let end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;

    fileReader.readAsArrayBuffer(blobSlice.call(file.file, start, end));
  }
}

function computeMD5Success(md5, file) {
  file.uniqueIdentifier = md5;
  file.resume();
}


// 组件挂载后执行
onMounted(() => {
  getList();
});
function storageFormatter(row) {
  let totalSize= row.totalSize;
  //1073741824为1G
  if(totalSize>=1073741824){
    return Math.round((row.totalSize/1073741824)*100)/100+"G"
  }else if(totalSize>=1048576){ //1048576为1M
    return  Math.round((row.totalSize/1048576)*100)/100+"M"
  }else{
    return  Math.round((row.totalSize/1024)*100)/100+"K"
  }
}
function upload() {
  console.log("upload")
  options.value.headers.Authorization = 'Bearer ' + getToken();
  if (uploadBtn.value) {
    $("#global-uploader-btn").click();
  }
}

function getList() {
  listFilelist(queryParams.value).then(response => {
    filelistList.value = response.rows;
    total.value = response.total;
  });
}



// 取消按钮
function cancel() {
  open.value = false;
  reset();
}


// 表单重置
function reset() {
  form.value = {
    id: undefined,
    filename: undefined,
    identifier: undefined,
    url: undefined,
    location: undefined,
    totalSize: undefined
  };
  this.resetForm("form");
}
/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
}

/** 提交按钮 */
const submitForm = () => {
  if (form.value.id != undefined) {
    updateFilelist(form.value).then(response => {
      if (response.code === 200) {
        this.msgSuccess("修改成功");
        open.value = false;
        getList();
      } else {
        this.msgError(response.msg);
      }
    });
  } else {
    addFilelist(this.form).then(response => {
      if (response.code === 200) {
        this.msgSuccess("新增成功");
        open.value = false;
        getList();
      } else {
        this.msgError(response.msg);
      }
    });
  }
}

/** 删除按钮操作 */
function handleDelete(row) {
  const ids = row.id || ids.value;
  this.$confirm('是否确认删除已上传文件列表编号为"' + ids + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(function() {
    return delFilelist(ids);
  }).then(() => {
    getList();
    this.msgSuccess("删除成功");
  }).catch(function() {});
}
/** 导出按钮操作 */
// function handleExport() {
//   const queryParams = this.queryParams;
//   this.$confirm('是否确认导出所有已上传文件列表数据项?', "警告", {
//     confirmButtonText: "确定",
//     cancelButtonText: "取消",
//     type: "warning"
//   }).then(function() {
//     return exportFilelist(queryParams);
//   }).then(response => {
//     this.download(response.msg);
//   }).catch(function() {});
// }

/** 下载操作 因上传文件较大时，下载时也需要更多的时间，所以自定义超时时间 */
function handleDownload(row) {
  //const queryParams = this.queryParams;
  const loca=row.location;
  this.download('system/filelist/download', {
    'location':loca
  }, row.filename,{timeout: 180000})
}
</script>
