package com.unipad.brain.personal.bean;

import com.unipad.brain.R;

/**
 * 柱状图实体类
 */
public class HistogramEntity implements Comparable<HistogramEntity> {

	private String histogramName;// 柱体名称，即X轴上显示的内容
	private int histogramValue;// 柱体值：根据此值算出柱体的高度
	private int histogramColor = R.color.red;// 柱体颜色，默认值为红色。

	/**
	 * @param histogramName
	 *            柱体名称，名称的长度最好不超过四个中文汉字的长度
	 * @param histogramValue
	 *            柱体值：根据此值算出柱体的高度
	 */
	public HistogramEntity(String histogramName, int histogramValue) {
		this.histogramName = histogramName;
		this.histogramValue = histogramValue;
	}

	public void setHistogramColor(int histogramColor) {
		this.histogramColor = histogramColor;
	}

	public String getHistogramName() {
		return histogramName;
	}

	public int getHistogramValue() {
		return histogramValue;
	}

	// 定义对象的比较规则：按histogramValue值降序排列
	@Override
	public int compareTo(HistogramEntity another) {
		if (this.histogramValue < another.histogramValue) {
			return 1;// 小于返回1，在这里可以把1看成true
		} else if (this.histogramValue > another.histogramValue) {
			return -1;
		} else {// 相等：可以在这里根据另一个值进行二次比较。
			return 0;
		}
	}

	@Override
	public String toString() {
		return "HistogramEntity [histogramName=" + histogramName
				+ ", histogramValue=" + histogramValue + ", histogramColor="
				+ histogramColor + "]";
	}

}
