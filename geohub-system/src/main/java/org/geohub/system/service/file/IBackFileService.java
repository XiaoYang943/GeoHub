package org.geohub.system.service.file;




import org.geohub.system.domain.BackChunk;
import org.geohub.system.domain.BackFilelist;
import org.geohub.system.domain.vo.CheckChunkVO;

import javax.servlet.http.HttpServletResponse;

public interface IBackFileService {

    int postFileUpload(BackChunk chunk, HttpServletResponse response);

    CheckChunkVO getFileUpload(BackChunk chunk, HttpServletResponse response);

    int deleteBackFileByIds(Long id);

    String mergeFile(BackFilelist fileInfo);
}
