package matching.sample.Fragment.Matching;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import matching.sample.Fragment.BaseFragment;
import matching.sample.Fragment.Matching.Chat.ChatFragment;
import matching.sample.Http.DataModel.UserData;
import matching.sample.Http.Requester.GetUserRequester;
import matching.sample.R;
import matching.sample.System.SaveData;

public class MatchingFragment extends BaseFragment {

    private MatchingAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_matching, null);

        // ListViewを初期化
        initListView(view);

        // ListViewを更新
        reloadListView();

        return view;
    }

    // ListViewを初期化する
    private void initListView(View view) {

        mAdapter = new MatchingAdapter(getActivity());

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);

        // クリックイベントを設定
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {

                // 該当行のユーザ情報を取り出し
                UserData userData = (UserData)parent.getItemAtPosition(position);

                // チャット画面に遷移
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.set(userData);
                getTabbarFragment().stackFragment(chatFragment, AnimationType.horizontal);
            }
        });
    }

    // ListViewを更新する
    private void reloadListView() {

        mAdapter.clear();

        UserData myUserData = GetUserRequester.getInstance().query(SaveData.getInstance().userId);

        // 相互に「いいね！」を送信し合ったユーザを表示する
        for (String likeTargetId : myUserData.likeTargetIds) {
            UserData userData = GetUserRequester.getInstance().query(likeTargetId);
            if (userData != null) {
                if (userData.likeTargetIds.contains(SaveData.getInstance().userId)) {
                    mAdapter.add(userData);
                }
            }
        }

        // データ更新
        mAdapter.notifyDataSetChanged();
    }

    // 表示を更新する
    public void reload() {
        reloadListView();
    }
}
