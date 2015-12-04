/**
 * 
 */
package net.thepaca.hunydoo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author MomNDad
 *
 */ 

public class HunyDewRecordTaskListItemView extends TextView {

	private Paint marginPaint;
	private Paint linePaint;
	private int paperColor;
	private float margin;

	/**
	 * @param context
	 */
	public HunyDewRecordTaskListItemView(Context context) {
		super(context);
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public HunyDewRecordTaskListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public HunyDewRecordTaskListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	
	private void init() {
		
		// get a reference to the resource table
		Resources myResources = getResources();
		
		// create the paint brushes we will use in the onDraw method
		marginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		marginPaint.setColor(myResources.getColor(R.color.HD_Margin));
		
		// now do the same for pant brush for the line
		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(myResources.getColor(R.color.HD_Lines));
		
		// Get the paper background color and the margin width
		paperColor = myResources.getColor(R.color.HD_Paper);
		margin = myResources.getDimension(R.dimen.HD_Margin);
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		
		//color as paper
		canvas.drawColor(paperColor);
		
		//Draw ruled lines
		canvas.drawLine(0, 0, getMeasuredHeight(), 0, linePaint);
		canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), 
							getMeasuredHeight(), linePaint);
		
		//draw margin
		canvas.drawLine(margin, 0, margin,getMeasuredHeight(),marginPaint);
		
		// move the text across from the margin
		canvas.save();
		canvas.translate(margin, 0);
		
		// using the base TextView to render the text
		super.onDraw(canvas);
		
		canvas.restore();
	}
}
