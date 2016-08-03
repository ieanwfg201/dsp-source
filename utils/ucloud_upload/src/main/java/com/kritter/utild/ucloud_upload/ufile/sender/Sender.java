package com.kritter.utild.ucloud_upload.ufile.sender;

import com.kritter.utild.ucloud_upload.ufile.UFileClient;
import com.kritter.utild.ucloud_upload.ufile.UFileRequest;
import com.kritter.utild.ucloud_upload.ufile.UFileResponse;

public interface Sender {

	public void makeAuth(UFileClient client, UFileRequest request);
	public UFileResponse send(UFileClient ufileClient, UFileRequest request);
}
