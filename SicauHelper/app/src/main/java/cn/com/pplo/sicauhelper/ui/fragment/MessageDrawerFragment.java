package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.CountCallback;

import java.util.Arrays;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.action.CommentAction;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.service.MessageService;
import cn.com.pplo.sicauhelper.ui.CommentActivity;

/**
 * Created by Administrator on 2014/11/26.
 */
public class MessageDrawerFragment extends Fragment {

    private ListView listView;
    private String[] messageType;
    private String objectId;

    public static MessageDrawerFragment newInstance() {
        MessageDrawerFragment fragment = new MessageDrawerFragment();
        return fragment;
    }

    public MessageDrawerFragment(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_drawer, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(getActivity(), view);
    }

    private void setUp(final Context context, View view) {
        listView = (ListView) view.findViewById(R.id.message_listView);
        messageType = getResources().getStringArray(R.array.message_type);
        objectId = SicauHelperApplication.getStudent().getObjectId();

        listView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item_message_list, R.id.message_type_tv, Arrays.asList(messageType)));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //点击商品发出评论
                if(position == 0){
                    CommentActivity.startCommentActivity(context, objectId, CommentAction.GOODS_SEND_COMMENT, messageType[position]);
                }
                //点击商品收到评论
                else if(position == 1){
                    CommentActivity.startCommentActivity(context, objectId, CommentAction.GOODS_RECEIVE_COMMENT, messageType[position]);

                }
                //点击帖子发出评论
                else if(position == 2){
                    CommentActivity.startCommentActivity(context, objectId, CommentAction.STATUS_SEND_COMMENT, messageType[position]);

                }
                //点击帖子收到评论
                else if(position == 3){
                    CommentActivity.startCommentActivity(context, objectId, CommentAction.STATUS_RECEIVE_COMMENT, messageType[position]);
                }
            }
        });
    }
}
