package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Score;


public class ScoreDetailFragment extends Fragment {
    private ListView listView;
    private List<Score> scores;
    private ScoreListAdapter scoreListAdapter;

    public static ScoreDetailFragment newInstance(List<Score> scoreList) {
        ScoreDetailFragment fragment = new ScoreDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (java.io.Serializable) scoreList);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ScoreDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached("成绩");
        scores = (List<Score>) getArguments().get("list");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_score_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    private void setUp(View view) {
        listView = (ListView) view.findViewById(R.id.score_listView);
        scoreListAdapter = new ScoreListAdapter(getActivity());
        scoreListAdapter.setData(scores);
        listView.setAdapter(scoreListAdapter);

        //此处需要修改

//        Map<String, String> params = new HashMap<String, String>();
//        Student student = SicauHelperApplication.getStudent();
//        if (student != null) {
//            params.put("user", student.getSid() + "");
//            params.put("pwd", student.getPswd());
//            NetUtil.getScoreHtmlStr(getActivity(), params, new NetUtil.NetCallbcak(getActivity()) {
//                @Override
//                public void onResponse(String result) {
//                    super.onResponse(result);
//                    Log.d("winson", "成绩" + result);
//                    StringUtil.parseScoreInfo(result, new StringUtil.Callback() {
//                        @Override
//                        public void handleParseResult(List<Score> tempList) {
//                            if(tempList != null){
//                                scores.clear();
//                                scores.addAll(tempList);
//                                scoreListAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                    super.onErrorResponse(volleyError);
//                }
//            });
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    private class ScoreListAdapter extends BaseAdapter {

        private Context context;
        private List<Score> data;

        public ScoreListAdapter(Context context) {
            this.context = context;
        }

        public void setData(List<Score> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Score getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //若是上半学期则在左边，反之则在右边
            if(String.valueOf(getItem(position).getGrade()).contains(".1")){
                    convertView = View.inflate(context, R.layout.item_fragment_score_list, null);
            }
            else {
                convertView = View.inflate(context, R.layout.item_fragment_score_list_right, null);
            }
            TextView categoryTv = (TextView) convertView.findViewById(R.id.category_tv);
            TextView gradeTv = (TextView) convertView.findViewById(R.id.grade_tv);
            final TextView scoreView = (TextView) convertView.findViewById(R.id.score_tv);
            TextView courseTv = (TextView) convertView.findViewById(R.id.course_tv);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
            TextView creditTv = (TextView) convertView.findViewById(R.id.credit_tv);

            String category = getItem(position).getCategory();
            int circleShape = 0;
            int color = 0;
            if(category.equals("必修")){
                circleShape = R.drawable.circle_blue;
                color = getResources().getColor(android.R.color.holo_blue_light);
            }
            else if(category.equals("公选")){
                circleShape = R.drawable.circle_red;
                color = getResources().getColor(android.R.color.holo_red_light);
            }
            else if(category.equals("任选")){
                circleShape = R.drawable.circle_green;
                color = getResources().getColor(android.R.color.holo_green_light);
            }
            else if(category.equals("推选")){
                circleShape = R.drawable.circle_orange;
                color = getResources().getColor(android.R.color.holo_orange_light);
            }
            else if(category.equals("实践")){
                circleShape = R.drawable.circle_purple;
                color = getResources().getColor(android.R.color.holo_purple);
            }
            scoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RotateAnimation animation = new RotateAnimation(0, 360);
                    animation.setDuration(500);

                    Log.d("winson", "点击了...");
                    scoreView.startAnimation(animation);
                }
            });

            categoryTv.setTextColor(color);
            scoreView.setBackgroundResource(circleShape);
            scoreView.setTextColor(Color.WHITE);
            gradeTv.setText(getItem(position).getGrade() + "");
            scoreView.setText(getItem(position).getMark() + "");
            courseTv.setText(getItem(position).getCourse() + "");
            creditTv.setText(getItem(position).getCredit() + "学分");
            categoryTv.setText("#" + getItem(position).getCategory() + "");
            return convertView;
        }
    }

}
