package cn.com.pplo.sicauhelper.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.feedback.Comment;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.avos.avoscloud.feedback.FeedbackThread;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.util.ImageUtil;
import cn.com.pplo.sicauhelper.util.SharedPreferencesUtil;
import cn.com.pplo.sicauhelper.util.TimeUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import de.hdodenhof.circleimageview.CircleImageView;

public class FeedbackActivity extends BaseActivity {

    public static final String SPLIT_STR = "--##WiFiMore##--";

    private ListView listView;
    private EditText commentEt;
    private Button commentBtn;
    private AlertDialog progressDialog;

    private List<Comment> data = new ArrayList<Comment>();
    private FeedbackAdapter feedbackAdapter;
    public static void startFeedbackActivity(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setUp(this);
    }

    private void setUp(Context context) {
        getSupportActionBar().setTitle("反馈");
        listView = (ListView) findViewById(R.id.feedback_listView);
        feedbackAdapter = new FeedbackAdapter(this, data);
        listView.setAdapter(feedbackAdapter);
        commentEt = (EditText) findViewById(R.id.reply_et);
        commentBtn = (Button) findViewById(R.id.reply_btn);
        progressDialog = UIUtil.getProgressDialog(context, "发送中...", false);

        FeedbackAgent agent = new FeedbackAgent(context);
        FeedbackThread feedbackThread = agent.getDefaultThread();
        sync(feedbackThread, false);
        sendComment(this, feedbackThread);
    }

    /**
     * 发送反馈
     */
    private void sendComment(final Context context, final FeedbackThread feedbackThread) {
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.hideSoftKeyboard(context, commentBtn);
                progressDialog.show();
                String str = commentEt.getText().toString().trim();
                if (TextUtils.isEmpty(str)) {
                    UIUtil.showShortToast(context, "反馈不可为空");
                } else {
                    //投诉人
                    feedbackThread.setContact("【建议人："
                            + SicauHelperApplication.getStudent().getString(TableContract.TableUser._NAME)
                            + "(" + SicauHelperApplication.getStudent().getString(TableContract.TableUser._SID)  + ")" +"】");
                    Comment newComment = new Comment();
                    //设置投诉者的类型
                    newComment.setCommentType(Comment.CommentType.USER);
                    //创建时间
                    newComment.setCreatedAt(new Date());
                    newComment.setType("-1");
                    newComment.setContent("【建议】" + str);
                    feedbackThread.add(newComment);
                    sync(feedbackThread, true);

                }
            }
        });
    }

    /**
     * 同步并更新反馈信息
     * @param feedbackThread
     */
    private void sync(final FeedbackThread feedbackThread, final boolean isSend) {

        feedbackThread.sync(new FeedbackThread.SyncCallback() {
            @Override
            public void onCommentsSend(List<Comment> comments, AVException e) {
                if (isSend) {
                    notifyFeedbackDataChanged(feedbackThread);
                }
                UIUtil.dismissProgressDialog(progressDialog);

                if(e == null) {
                    commentEt.setText("");

                }
                else {
                    UIUtil.showShortToast(FeedbackActivity.this, "发送反馈出了点问题，再试试");
                }
            }

            @Override
            public void onCommentsFetch(List<Comment> comments, AVException e) {
                if (!isSend) {
                    notifyFeedbackDataChanged(feedbackThread);
                }
            }
        });
    }

    /**
     * 通知数据变更
     * @param feedbackThread
     */
    private void notifyFeedbackDataChanged(FeedbackThread feedbackThread) {
        data.clear();
        data.addAll(feedbackThread.getCommentsList());
        feedbackAdapter.notifyDataSetChanged();
        Log.d("winson", "将存储：" + feedbackThread.getCommentsList().size());
        SharedPreferencesUtil.put(this, SharedPreferencesUtil.CURRENT_FEEDBACK_SIZE, feedbackThread.getCommentsList().size());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private class FeedbackAdapter extends BaseAdapter {

        private Context context;
        private List<Comment> commentList;

        private FeedbackAdapter(Context context, List<Comment> replyList) {
            this.context = context;
            this.commentList = replyList;
        }

        @Override
        public int getCount() {
            return commentList.size();
        }

        @Override
        public Comment getItem(int position) {
            return commentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.item_feedback_reply_list, null);
                holder.contentTv = (TextView) convertView.findViewById(R.id.reply_content_tv);
                holder.dateTv = (TextView) convertView.findViewById(R.id.reply_date_tv);
                holder.contentRightTv = (TextView) convertView.findViewById(R.id.reply_content_right_tv);
                holder.dateRightTv = (TextView) convertView.findViewById(R.id.reply_date_right_tv);
                holder.leftLayout = convertView.findViewById(R.id.left_layout);
                holder.rightLayout = convertView.findViewById(R.id.right_layout);
                holder.wifimoreIv = (CircleImageView) convertView.findViewById(R.id.reply_wifimore_iv);
                holder.userIv = (CircleImageView) convertView.findViewById(R.id.reply_user_head_iv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Comment comment = getItem(position);
            String content = comment.getContent();
            if (comment.getCommentType() == Comment.CommentType.USER) {
                holder.userIv.setVisibility(View.VISIBLE);
                holder.wifimoreIv.setVisibility(View.GONE);
                holder.leftLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.VISIBLE);
            } else {
                holder.userIv.setVisibility(View.GONE);
                holder.wifimoreIv.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.rightLayout.setVisibility(View.GONE);
            }
            holder.contentTv.setText(content);
            holder.contentRightTv.setText(content);
            holder.dateTv.setText(TimeUtil.dateToChinaTime(comment.getCreatedAt()));
            holder.dateRightTv.setText(TimeUtil.dateToChinaTime(comment.getCreatedAt()));
            holder.wifimoreIv.setImageResource(R.drawable.ic_launcher);
            String url = SicauHelperApplication.getStudent().getAVFile(TableContract.TableUser._PROFILE_URL).getUrl();
            if(!TextUtils.isEmpty(url)) {
                ImageLoader.getInstance().displayImage(url, holder.userIv, ImageUtil.getDisplayProfileOption(context));
            }
            return convertView;
        }

        private class ViewHolder {
            TextView contentTv;
            TextView dateTv;
            TextView contentRightTv;
            TextView dateRightTv;
            CircleImageView wifimoreIv;
            CircleImageView userIv;
            View leftLayout;
            View rightLayout;
        }
    }
}
