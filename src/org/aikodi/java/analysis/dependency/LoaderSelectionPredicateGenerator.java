package org.aikodi.java.analysis.dependency;

import java.util.Set;

import org.aikodi.chameleon.analysis.predicate.IsBinary;
import org.aikodi.chameleon.analysis.predicate.IsSource;
import org.aikodi.chameleon.core.element.Element;
import org.aikodi.chameleon.ui.widget.tree.DocumentScannerNode;
import org.aikodi.chameleon.ui.widget.tree.TreeNode;
import org.aikodi.chameleon.ui.widget.tree.TristateTreePruner;
import org.aikodi.chameleon.ui.widget.tree.DocumentScannerContentProvider.BinaryNode;
import org.aikodi.chameleon.ui.widget.tree.DocumentScannerContentProvider.ProjectNode;
import org.aikodi.chameleon.ui.widget.tree.DocumentScannerContentProvider.ScannerGroupNode;
import org.aikodi.chameleon.ui.widget.tree.DocumentScannerContentProvider.SourceNode;
import org.aikodi.chameleon.workspace.DocumentScanner;
import org.aikodi.rejuse.action.Nothing;
import org.aikodi.rejuse.data.tree.TreePredicate;
import org.aikodi.rejuse.predicate.UniversalPredicate;

public class LoaderSelectionPredicateGenerator extends TristateTreePruner<Object, Element> {
	
	public LoaderSelectionPredicateGenerator(TristateTreePruner<Object, Element> next) {
		super(next);
	}

	@Override
	protected TreePredicate<? super Element, Nothing> grayed(
			TreeNode<?,Object> node, 
			Set<TreeNode<?,Object>> checked, 
			Set<TreeNode<?,Object>> grayed, 
			TristateTreePruner<Object,Element>  first) {
		TreePredicate<? super Element, Nothing> result = null;
		if(node instanceof ProjectNode || node instanceof ScannerGroupNode) {
			result = first.childrenDisjunction(node, checked, grayed, first);
		} else if(node instanceof DocumentScannerNode) {
			DocumentScanner currentLoader = ((DocumentScannerNode)node).domainObject();
			result = new LoadedBy(Element.class, currentLoader).and((TreePredicate)first.childrenDisjunction(node, checked, grayed, first));
		} 
		return result;
	}

	@Override
	protected TreePredicate<? super Element, Nothing> checked(
			TreeNode<?,Object> node, 
			Set<TreeNode<?,Object>> checked, 
			Set<TreeNode<?,Object>> grayed, 
			TristateTreePruner<Object,Element>  first) {
		if(node instanceof SourceNode) {
			return new IsSource();
		} else if(node instanceof BinaryNode) {
			return new IsBinary();
		} else if (node instanceof DocumentScannerNode) {
			DocumentScanner currentLoader = ((DocumentScannerNode)node).domainObject();
			return new LoadedBy(Element.class, currentLoader);
		} 
		return null;
	}
}