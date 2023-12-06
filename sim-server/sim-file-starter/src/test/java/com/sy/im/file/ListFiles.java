package com.sy.im.file;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

public class ListFiles {

    public static void main(String[] args) throws Exception {
        // Endpoint�Ի���1�����ݣ�Ϊ��������Region�밴ʵ�������д��
        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        // �ӻ��������л�ȡ����ƾ֤�����б�����ʾ��֮ǰ����ȷ�������û�������OSS_ACCESS_KEY_ID��OSS_ACCESS_KEY_SECRET��
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // ��дBucket���ƣ�����examplebucket��
        String bucketName = "examplebucket";

        // ����OSSClientʵ����
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

        try {
            // ossClient.listObjects����ObjectListingʵ���������˴�listObject����ķ��ؽ����
            ObjectListing objectListing = ossClient.listObjects(bucketName);
            // objectListing.getObjectSummaries��ȡ�����ļ���������Ϣ��
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                System.out.println(" - " + objectSummary.getKey() + "  " +
                        "(size = " + objectSummary.getSize() + ")");
            }
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}