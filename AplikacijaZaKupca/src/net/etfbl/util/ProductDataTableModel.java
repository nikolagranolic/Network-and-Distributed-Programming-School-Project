package net.etfbl.util;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import net.etfbl.model.Product;

@SuppressWarnings("serial")
public class ProductDataTableModel extends AbstractTableModel {
	private ArrayList<Product> products;
	
	public ProductDataTableModel(ArrayList<Product> products) {
		this.products = products;
	}
	
	@Override
	public int getRowCount() {
		return products.size();
	}

	@Override
    public String getColumnName(int column) {
        String name = "??";
        switch (column) {
            case 0:
                name = "Product ID";
                break;
            case 1:
                name = "Product Name";
                break;
        }
        return name;
    }
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Product product = products.get(rowIndex);
		Object value = null;
		switch (columnIndex) {
	        case 0:
	            value = product.getProductId();
	            break;
	        case 1:
	            value = product.getProductName();
	            break;
		}
		return value;
	}
}
