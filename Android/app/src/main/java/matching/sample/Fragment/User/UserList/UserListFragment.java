package matching.sample.Fragment.User.UserList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import matching.sample.Fragment.BaseFragment;
import matching.sample.Fragment.User.UserDetail.UserDetailFragment;
import matching.sample.Http.DataModel.UserData;
import matching.sample.Http.Requester.GetUserRequester;
import matching.sample.R;
import matching.sample.System.SaveData;

public class UserListFragment extends BaseFragment {

    private UserListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {

        View view = inflater.inflate(R.layout.fragment_user_list, null);

        // GridViewを初期化
        initGridView(view);

        // データ更新
        reloadGridView();

        return view;
    }

    // GridViewを初期化する
    private void initGridView(View view) {

        mAdapter = new UserListAdapter(getActivity());

        GridView gridView = view.findViewById(R.id.gridView);
        gridView.setAdapter(mAdapter);

        // クリックイベントの設定
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {

                // 該当行のユーザ情報を取り出し
                UserData userData = (UserData)parent.getItemAtPosition(position);

                // ユーザ詳細に遷移
                UserDetailFragment userDetailFragment = new UserDetailFragment();
                userDetailFragment.set(userData);
                getTabbarFragment().stackFragment(userDetailFragment, AnimationType.horizontal);
            }
        });
    }

    // 表示を更新する
    private void reloadGridView() {

        mAdapter.clear();

        // 自身以外の全てのユーザ情報を表示する
        for (UserData userData : GetUserRequester.getInstance().dataList) {
            if (!userData.id.equals(SaveData.getInstance().userId)) {
                mAdapter.add(userData);
            }
        }

        // データ更新
        mAdapter.notifyDataSetChanged();
    }

    // 表示を更新する
    public void reload() {
        reloadGridView();
    }
}
