package cn.com.pplo.sicauhelper.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.ui.fragment.GalleryFragment;
import cn.com.pplo.sicauhelper.widget.GalleryPager;


public class GalleryActivity extends ActionBarActivity implements GalleryFragment.OnRectfChangeListener {

	private ActionBar actionBar;
	private GalleryPager galleryViewPager;
	private float width;

    public static final String EXTRA_IMAGE_URLS = "image_urls";
    public static final String EXTRA_POSITION = "image_position";

    private String[] imageUrls;
    private int position;

    public static void startGalleryActivity(Context context, String[] imageUrls, int position) {
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtra(EXTRA_IMAGE_URLS, imageUrls);
        intent.putExtra(EXTRA_POSITION, position);
        context.startActivity(intent);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_gallery);
		
		width = getResources().getDisplayMetrics().widthPixels;
        imageUrls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        position = getIntent().getIntExtra(EXTRA_POSITION, 0);
        initView();
		
	}

	private void initView() {

		actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
		actionBar.setTitle((position + 1) + "/" + imageUrls.length);

		galleryViewPager = (GalleryPager) findViewById(R.id.gallery_pager);
		final GalleryAdapter galleryAdapter = new GalleryAdapter(getSupportFragmentManager());
		galleryViewPager.setAdapter(galleryAdapter);
		galleryViewPager.setCurrentItem(position);
		galleryViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				actionBar.setTitle((position + 1) + "/" + imageUrls.length);
			}
			
			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {

			}
			
			@Override
			public void onPageScrollStateChanged(int position) {

			}
		});
	}

	
    private	class GalleryAdapter extends FragmentPagerAdapter {

		public GalleryAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return GalleryFragment.newInstance(imageUrls[position]);
		}

		@Override
		public int getCount() {
			return imageUrls.length;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
	@Override
	public void onRectfChanged(RectF rectF) {
		// TODO Auto-generated method stub
		if(rectF.left >= 0 || rectF.right <= width){
			if(galleryViewPager.isLocked()){
				galleryViewPager.setLocked(false);
			}
		}
		else {
			if(!galleryViewPager.isLocked()){
				galleryViewPager.setLocked(true);
			}
			
		}
	}
	
}
