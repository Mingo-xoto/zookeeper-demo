package com.yhq.zk;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.DataUpdater;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;

public class ZkClientTest {

	public static void main(String[] args) {
		ZkClient zkClient = new ZkClient("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183");
		String node = "/myapp";
		List<String> childrens = zkClient.getChildren("/sortede0000000009");
		zkClient.create("/sortede0000000009/children10", "fuck you,children11", ZooDefs.Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);

		zkClient.create("/sortede0000000009/children6", "fuck you,children6", Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);
		Object o = zkClient.readData("/sortede0000000009/children10");
		System.out.println(o);
		for (String children : childrens) {
			if ("children5".equals(children)) {
				zkClient.readData("/sortede0000000009/" + children, true);
			}
			System.out.println("children:" + children);
			List<String> c_childrens = zkClient.getChildren("/sortede0000000009/" + children);
			for (String c_children : c_childrens) {
				System.out.println("c_children：" + c_children);
			}
		}
		zkClient.getChildren(node);
		// 订阅监听事件
		childChangesListener(zkClient, node);
		dataChangesListener(zkClient, node);
		stateChangesListener(zkClient);

		if (!zkClient.exists(node)) {
			zkClient.createPersistent(node, "hello zookeeper");
		}
		o = zkClient.readData(node);
		System.out.println(o);

		zkClient.updateDataSerialized(node, new DataUpdater<String>() {

			public String update(String currentData) {
				return currentData + "-123";
			}
		});
		System.out.println(zkClient.readData(node));
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 订阅children变化
	 * 
	 * @param zkClient
	 * @param path
	 */
	public static void childChangesListener(ZkClient zkClient, final String path) {
		zkClient.subscribeChildChanges(path, new IZkChildListener() {

			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				System.out.println("clildren of path " + parentPath + ":" + currentChilds);
			}

		});
	}

	/**
	 * 订阅节点数据变化
	 * 
	 * @param zkClient
	 * @param path
	 */
	public static void dataChangesListener(ZkClient zkClient, final String path) {
		zkClient.subscribeDataChanges(path, new IZkDataListener() {

			public void handleDataChange(String dataPath, Object data) throws Exception {
				System.out.println("Data of " + dataPath + " has changed.");
			}

			public void handleDataDeleted(String dataPath) throws Exception {
				System.out.println("Data of " + dataPath + " has changed.");
			}

		});
	}

	/**
	 * 订阅状态变化
	 * 
	 * @param zkClient
	 */
	public static void stateChangesListener(ZkClient zkClient) {
		zkClient.subscribeStateChanges(new IZkStateListener() {

			public void handleStateChanged(KeeperState state) throws Exception {
				System.out.println("handleStateChanged");
			}

			public void handleSessionEstablishmentError(Throwable error) throws Exception {
				System.out.println("handleSessionEstablishmentError");
			}

			public void handleNewSession() throws Exception {
				System.out.println("handleNewSession");
			}
		});
	}
}
