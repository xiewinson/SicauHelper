package cn.com.pplo.sicauhelper.ui.fragment;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.nostra13.universalimageloader.core.ImageLoader;

public class GalleryFragment extends BaseFragment{

    public static final String IMAGE_URL = "image_url";

	private PhotoView imageView;
    private ActionBar actionBar;

    //图片地址
	private String url;
	private int width;
	private int height;
	private OnRectfChangeListener onRectfChangeListener;

    public static GalleryFragment newInstance(String url) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putString(IMAGE_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    public GalleryFragment(){
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		onRectfChangeListener = (OnRectfChangeListener) activity;
		url = getArguments().getString(IMAGE_URL);
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return initView(inflater, container, savedInstanceState);
	}

	private View initView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_gallery, container, false);
		actionBar = getSupportActionBar(getActivity());
		imageView = (PhotoView) view.findViewById(R.id.iv_photo);
        setPhotoView();
        return view;
	}


	private void setPhotoView() {
		imageView.setOnMatrixChangeListener(new OnMatrixChangedListener() {
			
			@Override
			public void onMatrixChanged(RectF rectF) {
				onRectfChangeListener.onRectfChanged(rectF);
			}
		});

        ImageLoader.getInstance().displayImage(url, imageView, ImageUtil.getDisplayImageOption(getActivity()));
		imageView.setOnPhotoTapListener(new OnPhotoTapListener() {
			
			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				if(actionBar.isShowing()){
					actionBar.hide();
				}
				else {
					actionBar.show();
				}
			}
		});
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

			}
		});
	}
	
	public void restoreImageView(){
		if(imageView != null){
			imageView.setScaleType(ScaleType.CENTER);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public interface OnRectfChangeListener{
		void onRectfChanged(RectF rectF);
	}
}
