package site.lifd.core.lang.tree;

import site.lifd.core.collection.IterUtil;
import site.lifd.core.lang.tree.parser.DefaultNodeParser;
import site.lifd.core.lang.tree.parser.NodeParser;
import site.lifd.core.util.ObjectUtil;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树工具类
 *
 * */
public class TreeUtil {

	/**
	 * 构建单root节点树
	 *
	 * @param list 源数据集合
	 * @return {@link Tree}
	 *
	 */
	public static Tree<Integer> buildSingle(List<TreeNode<Integer>> list) {
		return buildSingle(list, 0);
	}

	/**
	 * 树构建
	 *
	 * @param list 源数据集合
	 * @return List
	 */
	public static List<Tree<Integer>> build(List<TreeNode<Integer>> list) {
		return build(list, 0);
	}

	/**
	 * 构建单root节点树<br>
	 * 它会生成一个以指定ID为ID的空的节点，然后逐级增加子节点。
	 *
	 * @param <E>      ID类型
	 * @param list     源数据集合
	 * @param parentId 最顶层父id值 一般为 0 之类
	 * @return {@link Tree}
	 *
	 */
	public static <E> Tree<E> buildSingle(List<TreeNode<E>> list, E parentId) {
		return buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, new DefaultNodeParser<>());
	}

	/**
	 * 树构建
	 *
	 * @param <E>      ID类型
	 * @param list     源数据集合
	 * @param parentId 最顶层父id值 一般为 0 之类
	 * @return List
	 */
	public static <E> List<Tree<E>> build(List<TreeNode<E>> list, E parentId) {
		return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, new DefaultNodeParser<>());
	}

	/**
	 * 构建单root节点树<br>
	 * 它会生成一个以指定ID为ID的空的节点，然后逐级增加子节点。
	 *
	 * @param <T>        转换的实体 为数据源里的对象类型
	 * @param <E>        ID类型
	 * @param list       源数据集合
	 * @param parentId   最顶层父id值 一般为 0 之类
	 * @param nodeParser 转换器
	 * @return {@link Tree}
	 *
	 */
	public static <T, E> Tree<E> buildSingle(List<T> list, E parentId, NodeParser<T, E> nodeParser) {
		return buildSingle(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
	}

	/**
	 * 树构建
	 *
	 * @param <T>        转换的实体 为数据源里的对象类型
	 * @param <E>        ID类型
	 * @param list       源数据集合
	 * @param parentId   最顶层父id值 一般为 0 之类
	 * @param nodeParser 转换器
	 * @return List
	 */
	public static <T, E> List<Tree<E>> build(List<T> list, E parentId, NodeParser<T, E> nodeParser) {
		return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
	}

	/**
	 * 树构建
	 *
	 * @param <T>            转换的实体 为数据源里的对象类型
	 * @param <E>            ID类型
	 * @param list           源数据集合
	 * @param rootId         最顶层父id值 一般为 0 之类
	 * @param treeNodeConfig 配置
	 * @param nodeParser     转换器
	 * @return List
	 */
	public static <T, E> List<Tree<E>> build(List<T> list, E rootId, TreeNodeConfig treeNodeConfig, NodeParser<T, E> nodeParser) {
		return buildSingle(list, rootId, treeNodeConfig, nodeParser).getChildren();
	}

	/**
	 * 构建单root节点树<br>
	 * 它会生成一个以指定ID为ID的空的节点，然后逐级增加子节点。
	 *
	 * @param <T>            转换的实体 为数据源里的对象类型
	 * @param <E>            ID类型
	 * @param list           源数据集合
	 * @param rootId         最顶层父id值 一般为 0 之类
	 * @param treeNodeConfig 配置
	 * @param nodeParser     转换器
	 * @return {@link Tree}
	 *
	 */
	public static <T, E> Tree<E> buildSingle(List<T> list, E rootId, TreeNodeConfig treeNodeConfig, NodeParser<T, E> nodeParser) {
		return TreeBuilder.of(rootId, treeNodeConfig)
				.append(list, nodeParser).build();
	}

	/**
	 * 树构建，按照权重排序
	 *
	 * @param <E>    ID类型
	 * @param map    源数据Map
	 * @param rootId 最顶层父id值 一般为 0 之类
	 * @return List
	 *
	 */
	public static <E> List<Tree<E>> build(Map<E, Tree<E>> map, E rootId) {
		return buildSingle(map, rootId).getChildren();
	}

	/**
	 * 单点树构建，按照权重排序<br>
	 * 它会生成一个以指定ID为ID的空的节点，然后逐级增加子节点。
	 *
	 * @param <E>    ID类型
	 * @param map    源数据Map
	 * @param rootId 根节点id值 一般为 0 之类
	 * @return {@link Tree}
	 *
	 */
	public static <E> Tree<E> buildSingle(Map<E, Tree<E>> map, E rootId) {
		final Tree<E> tree = IterUtil.getFirstNoneNull(map.values());
		if (null != tree) {
			final TreeNodeConfig config = tree.getConfig();
			return TreeBuilder.of(rootId, config)
					.append(map)
					.build();
		}

		return createEmptyNode(rootId);
	}

	/**
	 * 获取ID对应的节点，如果有多个ID相同的节点，只返回第一个。<br>
	 * 此方法只查找此节点及子节点，采用递归深度优先遍历。
	 *
	 * @param <T>  ID类型
	 * @param node 节点
	 * @param id   ID
	 * @return 节点
	 *
	 */
	public static <T> Tree<T> getNode(Tree<T> node, T id) {
		if (ObjectUtil.equal(id, node.getId())) {
			return node;
		}

		final List<Tree<T>> children = node.getChildren();
		if (null == children) {
			return null;
		}

		// 查找子节点
		Tree<T> childNode;
		for (Tree<T> child : children) {
			childNode = child.getNode(id);
			if (null != childNode) {
				return childNode;
			}
		}

		// 未找到节点
		return null;
	}

	/**
	 * 获取所有父节点名称列表
	 *
	 * <p>
	 * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
	 * 返回结果就是：[研发一部, 研发中心, 技术中心]
	 *
	 * @param <T>                节点ID类型
	 * @param node               节点
	 * @param includeCurrentNode 是否包含当前节点的名称
	 * @return 所有父节点名称列表，node为null返回空List
	 *
	 */
	public static <T> List<CharSequence> getParentsName(Tree<T> node, boolean includeCurrentNode) {
		final List<CharSequence> result = new ArrayList<>();
		if (null == node) {
			return result;
		}

		if (includeCurrentNode) {
			result.add(node.getName());
		}

		Tree<T> parent = node.getParent();
		CharSequence name;
		while (null != parent) {
			name = parent.getName();
			parent = parent.getParent();
			if(null != name || null != parent){
				// issue#I795IN，根节点的null不加入
				result.add(name);
			}
		}
		return result;
	}

	/**
	 *  获取所有父节点ID列表
	 *
	 * <p>
	 * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
	 * 返回结果就是：[研发部, 技术中心]
	 *
	 * @param <T>                节点ID类型
	 * @param node               节点
	 * @param includeCurrentNode 是否包含当前节点的名称
	 * @return 所有父节点ID列表，node为null返回空List
	 *
	 */
	public static <T> List<T> getParentsId(Tree<T> node, boolean includeCurrentNode) {
		final List<T> result = new ArrayList<>();
		if (null == node) {
			return result;
		}

		if (includeCurrentNode) {
			result.add(node.getId());
		}

		Tree<T> parent = node.getParent();
		T id;
		while (null != parent) {
			id = parent.getId();
			parent = parent.getParent();
			if(null != id || null != parent){
				// issue#I795IN，根节点的null不加入
				result.add(id);
			}
		}
		return result;
	}

	/**
	 * 创建空Tree的节点
	 *
	 * @param id  节点ID
	 * @param <E> 节点ID类型
	 * @return {@link Tree}
	 *
	 */
	public static <E> Tree<E> createEmptyNode(E id) {
		return new Tree<E>().setId(id);
	}

	/**
	 * 函数式构建树状结构(无需继承Tree类)
	 *
	 * @param nodes			需要构建树集合
	 * @param rootId		根节点ID
	 * @param idFunc		获取节点ID函数
	 * @param parentIdFunc	获取节点父ID函数
	 * @param setChildFunc	设置孩子集合函数
	 * @param <T>			节点ID类型
	 * @param <E>			节点类型
	 * @return List
	 */
	public static <T, E> List<E> build(List<E> nodes, T rootId,
									   Function<E, T> idFunc,
									   Function<E, T> parentIdFunc,
									   BiConsumer<E, List<E>> setChildFunc) {
		List<E> rootList = nodes.stream().filter(tree -> parentIdFunc.apply(tree).equals(rootId)).collect(Collectors.toList());
		Set<T> filterOperated = new HashSet<>(rootList.size() + nodes.size());
		//对每个根节点都封装它的孩子节点
		rootList.forEach(root -> setChildren(root, nodes, filterOperated, idFunc, parentIdFunc, setChildFunc));
		return rootList;
	}

	/**
	 * 封装孩子节点
	 *
	 * @param root				根节点
	 * @param nodes				节点集合
	 * @param filterOperated	过滤操作Map
	 * @param idFunc			获取节点ID函数
	 * @param parentIdFunc		获取节点父ID函数
	 * @param setChildFunc		设置孩子集合函数
	 * @param <T>				节点ID类型
	 * @param <E>				节点类型
	 */
	private static <T, E> void setChildren(E root, List<E> nodes, Set<T> filterOperated, Function<E, T> idFunc, Function<E, T> parentIdFunc, BiConsumer<E, List<E>> setChildFunc) {
		List<E> children = new ArrayList<>();
		nodes.stream()
			//过滤出未操作过的节点
			.filter(body -> !filterOperated.contains(idFunc.apply(body)))
			//过滤出孩子节点
			.filter(body -> Objects.equals(idFunc.apply(root), parentIdFunc.apply(body)))
			.forEach(body -> {
				filterOperated.add(idFunc.apply(body));
				children.add(body);
				//递归 对每个孩子节点执行同样操作
				setChildren(body, nodes, filterOperated, idFunc, parentIdFunc, setChildFunc);
			});
		setChildFunc.accept(root, children);
	}
}
