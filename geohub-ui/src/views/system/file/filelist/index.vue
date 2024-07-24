<template>
  <div class="app-container">
    <el-form ref="queryForm" :inline="true" :model="queryParams" label-width="68px">
      <el-form-item>
        <el-button v-hasPermi="['system:file:upload']" icon="el-icon-upload" size="mini" type="primary" @click="upload">
          上传
        </el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            v-hasPermi="['system:filelist:remove']"
            :disabled="multiple"
            icon="el-icon-delete"
            plain
            size="mini"
            type="danger"
            @click="handleDelete"
        >删除
        </el-button>
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
      <el-table-column align="center" type="selection" width="50"/>
      <el-table-column align="center" label="编号" prop="id"/>
      <el-table-column align="center" label="文件名" prop="filename"/>
      <el-table-column align="center" label="本地地址" prop="location"/>
      <el-table-column :formatter="storageFormatter" align="center" label="文件总大小" prop="totalSize"/>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template slot-scope="scope">
          <el-button
              icon="el-icon-edit"
              size="mini"
              type="text"
              @click="handleDownload(scope.row)"

          >下载
          </el-button>
        </template>
        <!--        v-hasPermi="['system:filelist:download']"-->
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :limit.sync="queryParams.pageSize"
        :page.sync="queryParams.pageNum"
        :total="total"
        @pagination="getList"
    />
  </div>

  <!-- 上传 -->
  <uploader
      ref="uploader"
      :autoStart="false"
      :options="options"
      class="uploader-app"
      @file-added="onFileAdded"
      @file-success="onFileSuccess"
      @file-progress="onFileProgress"
      @file-error="onFileError">
    <uploader-unsupport></uploader-unsupport>
    <uploader-btn v-show="false" id="global-uploader-btn" ref="uploadBtn" :attrs="attrs">选择文件</uploader-btn>
  </uploader>
</template>

<script lang="ts" setup>
import {fileMerge} from "@/api/system/file/fileuploader/fileuploader.js";
import SparkMD5 from "spark-md5";
import {onMounted, reactive, ref} from 'vue';
import {addFilelist, delFilelist, listFilelist, updateFilelist} from "@/api/system/file/filelist";
import {getToken} from '@/utils/auth';

const ACCEPT_CONFIG = {
  image: ['.png', '.jpg', '.jpeg', '.gif', '.bmp'],
  video: ['.mp4', '.rmvb', '.mkv', '.wmv', '.flv'],
  document: ['.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx', '.pdf', '.txt', '.tif', '.tiff', '.zip', '.7z', '.rar'],
  getAll() {
    return [...this.image, ...this.video, ...this.document]
  },
};
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
  filename: [{required: true, message: '文件名不能为空', trigger: 'blur'}],
  identifier: [{required: true, message: '唯一标识,MD5不能为空', trigger: 'blur'}],
  url: [{required: true, message: '链接不能为空', trigger: 'blur'}]
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
  query: () => {
  }
});

const attrs = ref({
  accept: ACCEPT_CONFIG.getAll()
});

const panelShow = ref(false);

const uploadBtn = ref(null);
const onFileAdded = (file) => {
  panelShow.value = true;
  computeMD5(file);
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
      console.log("res", res)
    }).catch(e => {
      console.log("合并异常,重新发起请求,文件名为:", file.name)
      //由于网络或服务器原因,导致合并过程中断线,此时如果不重新发起请求,就会进入失败的状态,导致该文件无法重试
      file.retry();
    });
    // 不需要合并
  } else {
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
    // this.error(`文件${file.name}读取出错，请检查该文件`)
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
  let totalSize = row.totalSize;
  //1073741824为1G
  if (totalSize >= 1073741824) {
    return Math.round((row.totalSize / 1073741824) * 100) / 100 + "G"
  } else if (totalSize >= 1048576) { //1048576为1M
    return Math.round((row.totalSize / 1048576) * 100) / 100 + "M"
  } else {
    return Math.round((row.totalSize / 1024) * 100) / 100 + "K"
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

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
}


/** 删除按钮操作 */
function handleDelete(row) {
  const ids = row.id || ids.value;
  this.$confirm('是否确认删除已上传文件列表编号为"' + ids + '"的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(function () {
    return delFilelist(ids);
  }).then(() => {
    getList();
    // this.msgSuccess("删除成功");
  }).catch(function () {
  });
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
  const loca = row.location;
  this.download('system/filelist/download', {
    'location': loca
  }, row.filename, {timeout: 180000})
}
</script>
