package com.gs.rpc.registry;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Etcd 官方案例
 * @program: rpc
 * @description:
 * @author: lydms
 * @create: 2024-04-10 20:59
 **/
public class EtcdDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Client client = Client.builder().endpoints("http://localhost:2379").build();
        KV kvClient = client.getKVClient(); // 操作etcd写入和读取数据
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        // put the key and value
        kvClient.put(key, value).get();

        // get
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);

        // 获取结果
        GetResponse response = getFuture.get();
        System.out.println(response.toString());
        kvClient.delete(key).get();
        kvClient.close();
        client.close();


    }
}
