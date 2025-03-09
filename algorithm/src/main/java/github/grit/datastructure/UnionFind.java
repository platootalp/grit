package github.grit.datastructure;

/**
 * 并查集
 */
public class UnionFind {
	// 并查集的根节点
	private final int[] parent;
	// 并查集的秩
	private final int[] rank;
	// 联通分量的个数
	private int count;

	public UnionFind(int n) {
		this.count = n;
		this.parent = new int[n];
		this.rank = new int[n];
		for (int i = 0; i < n; i++) {
			this.parent[i] = i;
			this.rank[i] = 1;
		}
	}

	public int find(int x) {
		if (parent[x] != x) {
			parent[x] = find(parent[x]);
		}
		return parent[x];
	}

	public void union(int x, int y) {
		int rootX = find(x);
		int rootY = find(y);
		if (rootX == rootY) {
			return;
		}
		if (rank[rootX] > rank[rootY]) {
			parent[rootY] = rootX;
		} else if (rank[rootX] < rank[rootY]) {
			parent[rootX] = rootY;
		} else {
			parent[rootY] = rootX;
			rank[rootX]++;
		}
		count--;
	}

	public int getCount() {
		return count;
	}

	// 判断两个节点是否连通
	public boolean connected(int x, int y) {
		return find(x) == find(y);
	}

	public static void main(String[] args) {
		UnionFind uf = new UnionFind(10);
		uf.union(0, 1);
		uf.union(1, 2);
		uf.union(3, 4);
		uf.union(5, 6);
		uf.union(6, 7);
		uf.union(7, 8);
		uf.union(8, 9);
		System.out.println(uf.connected(0, 2));
		System.out.println(uf.connected(0, 9));
		System.out.println(uf.getCount());
	}
}
