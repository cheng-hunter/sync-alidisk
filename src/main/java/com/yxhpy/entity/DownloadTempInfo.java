package com.yxhpy.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.yxhpy.entity.response.FileDownloadEntity;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadTempInfo implements Serializable {
	private String crc64;
	private String contHashName;
	private String contHash;
	/** 下载的文件大小 */
	private int size;
	/** 创建该文件时配置的段大小 */
	private int partSize;
	/** 已完成的段 */
	private Set<Integer> finish = new HashSet<>();

	public DownloadTempInfo(FileDownloadEntity downloadEntity, int partSize) {
		this.crc64 = downloadEntity.getCrc64Hash();
		this.contHashName = downloadEntity.getContentHashName();
		this.contHash = downloadEntity.getContentHash();
		this.partSize = partSize;
		this.size = downloadEntity.getSize();
	}

	public boolean checkFileInfo(FileDownloadEntity downloadEntity) {
		return Objects.equals(crc64, downloadEntity.getCrc64Hash())
				&& Objects.equals(contHashName,
						downloadEntity.getContentHashName())
				&& Objects.equals(contHash, downloadEntity.getContentHash())
				&& size == downloadEntity.getSize();
	}

	public String toSaveJson() {
		return JSONUtil.toJsonStr(this) + System.lineSeparator();
	}

	public boolean isFinish() {
		int partNum = size / partSize;
		if (size % partSize != 0) {
			partNum++;
		}
		return partNum == finish.size();
	}

}
