package com.yhq.zk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
		 App dm = new App();
		 dm.createZKInstance();
		 dm.ZKOperations();
		 dm.ZKClose();
		System.out.println(Integer.toBinaryString(-9));
		System.out.println(Integer.toBinaryString(-8));
		System.out.println(Integer.toBinaryString(7));
		System.out.println(Integer.toBinaryString(-1));
	}

	// 会话超时时间，设置为与系统默认时间一致
	private static final int SESSION_TIMEOUT = 30 * 1000;

	// 创建 ZooKeeper 实例
	private ZooKeeper zk;

	// 创建 Watcher 实例
	private Watcher watcher = new Watcher() {
		/**
		 * Watched事件
		 */
		public void process(WatchedEvent event) {
			System.out.println("WatchedEvent >>> " + event.toString());
		}
	};

	// 初始化 ZooKeeper 实例
	private void createZKInstance() throws IOException, KeeperException, InterruptedException {
		// 连接到ZK服务，多个可以用逗号分割写
		zk = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183", App.SESSION_TIMEOUT, this.watcher);
		if (!zk.getState().equals(States.CONNECTED)) {
			while (true) {
				if (zk.getState().equals(States.CONNECTED)) {
					break;
				}
				try {
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("sessionID:"+zk.getSessionId());
		System.out.println("sessionID:"+zk.getState());

	}

	private void ZKOperations() throws IOException, InterruptedException, KeeperException {
		System.out.println("\n1. 创建 ZooKeeper 节点 (znode ： zoo2, 数据： myData2 ，权限： OPEN_ACL_UNSAFE ，节点类型： Persistent");
		zk.create("/zoo2", "myData2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

		System.out.println("\n2. 查看是否创建成功： ");
		System.out.println(new String(zk.getData("/zoo2", this.watcher, null)));// 添加Watch

		zk.create("/sortede0000000009/children6", "fuck you,children1".getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.EPHEMERAL);
		System.out.println(new String(zk.getData("/sortede0000000009/children6", this.watcher, null)));

		// 前面一行我们添加了对/zoo2节点的监视，所以这里对/zoo2进行修改的时候，会触发Watch事件。
		System.out.println("\n3. 修改节点数据 ");
		zk.setData("/zoo2", "shanhy20160310".getBytes(), -1);

		// 这里再次进行修改，则不会触发Watch事件，这就是我们验证ZK的一个特性“一次性触发”，也就是说设置一次监视，只会对下次操作起一次作用。
		System.out.println("\n3-1. 再次修改节点数据 ");
		zk.setData("/zoo2", "shanhy20160310-ABCD".getBytes(), -1);

		System.out.println("\n4. 查看是否修改成功： ");
		System.out.println(new String(zk.getData("/zoo2", false, null)));

		System.out.println("\n5. 删除节点 ");
		// zk.delete("/zoo2", -1);

		System.out.println("\n6. 查看节点是否被删除： ");
		System.out.println(" 节点状态： [" + zk.exists("/zoo2", false) + "]");
	}

	private void ZKClose() throws InterruptedException {
		zk.close();
	}

}
