package xr.hellochartsdemo.ui.activity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import xr.hellochartsdemo.R;

/**
 * @author xiarui 2016.09.06
 * @description 线性图表的使用 （折线图、曲线图）
 */
public class LineChartActivity extends BaseActivity {

    /*=========== 控件相关 ==========*/
    private LineChartView mLineChartView;                   //线性图表控件

    /*=========== 数据相关 ==========*/
    private LineChartData mLineData;                    //图标数据
    private int numberOfLines = 1;                      //图上折线/曲线的显示条数
    private int maxNumberOfLines = 4;                   //图上折线/曲线的最多条数
    private int numberOfPoints = 12;                    //图上的节点数

    /*=========== 状态相关 ==========*/
    private boolean isHasAxes = true;                   //是否显示坐标轴
    private boolean isHasAxesNames = true;              //是否显示坐标轴名称
    private boolean isHasLines = true;                  //是否显示折线/曲线
    private boolean isHasPoints = true;                 //是否显示线上的节点
    private boolean isFilled = false;                   //是否填充线下方区域
    private boolean isHasPointsLables = false;           //是否显示节点上的标签信息
    private boolean isCubic = false;                    //是否是立体的
    private boolean isPointsHasSelected = false;        //设置节点点击后效果(消失/保持)
    private boolean isPointsHaveDifferentColor;         //节点是否有不同的颜色

    /*=========== 其他相关 ==========*/
    private ValueShape pointsShape = ValueShape.CIRCLE;       //点的形状(圆/方/菱形)
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints]; //将线上的点放在一个数组中

    @Override
    public int getLayoutId() {
        return R.layout.activity_line_chart;
    }

    @Override
    public void initView() {
        mLineChartView = (LineChartView) findViewById(R.id.lvc_main);
        /**
         * 禁用视图重新计算 主要用于图表在变化时动态更改，不是重新计算
         * 类似于ListView中数据变化时，只需notifyDataSetChanged()，而不用重新setAdapter()
         */
        mLineChartView.setViewportCalculationEnabled(false);
    }

    @Override
    public void initData() {
        setPointsValues();          //设置每条线的节点值
        setLinesDatas();            //设置每条线的一些属性
        resetViewport();            //计算并绘图
    }

    @Override
    public void initListener() {
        //节点点击事件监听
        mLineChartView.setOnValueTouchListener(new ValueTouchListener());
    }

    @Override
    public void processClick(View v) {
        //TODO：其他的按钮事件
    }

    /**
     * 利用随机数设置每条线上的节点的值
     */
    private void setPointsValues() {
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 100f;
            }
        }
    }

    /**
     * 设置线的相关数据
     */
    private void setLinesDatas() {
        List<Line> lines = new ArrayList<>();
        //循环将每条线都设置成对应的属性
        for (int i = 0; i < numberOfLines; ++i) {
            //节点的值
            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);               //根据值来创建一条线
            line.setColor(ChartUtils.COLORS[i]);        //设置线的颜色
            line.setShape(pointsShape);                 //设置线的形状
            line.setHasLines(isHasLines);               //设置是否显示线
            line.setHasPoints(isHasPoints);             //设置是否显示节点
            line.setCubic(isCubic);                     //设置线是否立体或其他效果
            line.setFilled(isFilled);                   //设置是否填充线下方区域
            line.setHasLabels(isHasPointsLables);       //设置是否显示节点标签
            line.setHasLabelsOnlyForSelected(isPointsHasSelected);      //设置节点点击的效果
            //如果节点有不同颜色 则设置不同的颜色
            if (isPointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        mLineData = new LineChartData(lines);               //将所有的线加入线数据类中
        mLineData.setBaseValue(Float.NEGATIVE_INFINITY);    //设置基准数(大概是数据范围)

        /* 其他的一些方法 可自行查看效果
         * mLineData.setValueLabelBackgroundAuto(true);            //设置数据背景是否跟随节点颜色
         * mLineData.setValueLabelBackgroundColor(Color.BLUE);     //设置数据背景颜色
         * mLineData.setValueLabelBackgroundEnabled(true);         //设置是否有数据背景
         * mLineData.setValueLabelsTextColor(Color.RED);           //设置数据文字颜色
         * mLineData.setValueLabelTextSize(15);                    //设置数据文字大小
         * mLineData.setValueLabelTypeface(Typeface.MONOSPACE);    //设置数据文字样式
        */

        //如果显示坐标轴
        if (isHasAxes) {
            Axis axisX = new Axis();                    //X轴
            Axis axisY = new Axis().setHasLines(true);  //Y轴
            //如果显示名称
            if (isHasAxesNames) {
                axisX.setName("Axis X");                //设置名称
                axisY.setName("Axis Y");
            }
            mLineData.setAxisXBottom(axisX);            //设置X轴位置 下方
            mLineData.setAxisYLeft(axisY);              //设置Y轴位置 左边
        } else {
            mLineData.setAxisXBottom(null);
            mLineData.setAxisYLeft(null);
        }

        mLineChartView.setLineChartData(mLineData);    //设置图标控件
    }

    /**
     * 重点方法，计算绘制图表
     */
    private void resetViewport() {
        //创建一个图标视图 大小为控件的最大大小
        final Viewport v = new Viewport(mLineChartView.getMaximumViewport());
        v.left = 0;
        v.bottom = 0;                       //坐标原点在左下
        v.top = 100;                        //最高点为100
        v.right = numberOfPoints - 1;       //右边为点 坐标从0开始 点号从1 需要 -1
        mLineChartView.setMaximumViewport(v);   //给最大的视图设置 相当于原图
        mLineChartView.setCurrentViewport(v);   //给当前的视图设置 相当于当前展示的图
    }

    /**
     * 菜单
     *
     * @param menu 菜单
     * @return true 显示
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_line_chart, menu);
        return true;
    }

    /**
     * 菜单选项
     *
     * @param item 菜单项
     * @return true 执行
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_line_reset:
                resetLines();
                return true;
            case R.id.menu_line_add:
                addLineToData();
                return true;
            case R.id.menu_line_show_hide_lines:

                return true;
            case R.id.menu_line_show_hide_points:

                return true;
            case R.id.menu_line_cubic:

                return true;
            case R.id.menu_line_fill_area:

                return true;
            case R.id.menu_line_point_color:

                return true;
            case R.id.menu_line_point_circle:

                return true;
            case R.id.menu_line_point_square:

                return true;
            case R.id.menu_line_point_diamond:

                return true;
            case R.id.menu_line_show_hide_axes:

                return true;
            case R.id.menu_line_show_hide_axes_name:

                return true;
            case R.id.menu_line_show_hide_lables:

                return true;
            case R.id.menu_line_animate:

                return true;
            case R.id.menu_line_point_select_mode:

                return true;
            case R.id.menu_line_touch_zoom:

                return true;
            case R.id.menu_line_touch_zoom_xy:

                return true;
            case R.id.menu_line_touch_zoom_x:

                return true;
            case R.id.menu_line_touch_zoom_y:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 重置线的格式 恢复初始化
     */
    private void resetLines() {
        numberOfLines = 1;
        /*===== 恢复初始化时相关属性 =====*/
        isHasAxesNames = true;
        isHasLines = true;
        isHasPoints = true;
        pointsShape = ValueShape.CIRCLE;
        isFilled = false;
        isHasPointsLables = false;
        isCubic = false;
        isPointsHasSelected = false;
        isPointsHaveDifferentColor = false;

        mLineChartView.setValueSelectionEnabled(isPointsHasSelected);
        resetViewport();        //重新计算
        setLinesDatas();        //再设置一次
    }

    private void addLineToData() {
        if (mLineData.getLines().size() >= maxNumberOfLines) {
            Toast.makeText(LineChartActivity.this, "最多只能有4条线", Toast.LENGTH_SHORT).show();
            return;
        } else {
            ++numberOfLines;
        }
        setLinesDatas();        //再设置一次
    }


    /**
     * 节点触摸监听
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(LineChartActivity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {

        }
    }
}