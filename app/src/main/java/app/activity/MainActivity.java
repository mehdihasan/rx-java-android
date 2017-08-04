package app.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.githubdemo.app.R;

import app.Data;
import app.adapter.CardAdapter;
import app.model.Github;
import app.service.GithubService;
import app.service.ServiceFactory;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Set up Android CardView/RecycleView
         */
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final CardAdapter mCardAdapter = new CardAdapter();
        mRecyclerView.setAdapter(mCardAdapter);

        /**
         * START: button set up
         */
        final Button bClear = (Button) findViewById(R.id.button_clear);
        Button bFetch = (Button) findViewById(R.id.button_fetch);
        bClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mCardAdapter.clear();
            }
        });

        bFetch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GithubService service = ServiceFactory.createRetrofitService(GithubService.class, GithubService.SERVICE_ENDPOINT);
                for(String userName : Data.githubList) {
                    service.getUser(userName)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Github>() {

                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onNext(@NonNull Github github) {
                                    mCardAdapter.addData(github);
                                    System.out.println("New data received: " + github.toString());
                                }

                                @Override
                                public void onError(Throwable e) {
                                    System.out.println("Error received: " + e.getMessage());
                                }

                                @Override
                                public void onComplete() {
                                    System.out.println("All data emitted.");
                                }
                            });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
