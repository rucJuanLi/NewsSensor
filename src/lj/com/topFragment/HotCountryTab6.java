package lj.com.topFragment;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import lj.com.Adapter.SampleListAdapter;
import lj.com.main.BrowseActivity;
import lj.com.main.R;
import lj.com.model.Topic;
import lj.com.service.CountryService;
import lj.com.utils.CheckNetState;
import lj.com.utils.ReadStreamCommon;
import lj.com.utils.ShowNetUnuse;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HotCountryTab6 extends Fragment  implements OnItemClickListener{
	private View rootView; 
	private ArrayList<Topic> ListItems;
	private Toast imagetoast;
	private PullToRefreshListView  mListView6;
	private SampleListAdapter mAdapter6;
    //private static final String Gettopiccluster_url = "http://websensor.playbigdata.com/fss3/service.svc/GetHotTopicClusters";
	private static final String URL = "http://websensor.playbigdata.com/fss3/service.svc/RetrieveHotTopicData?url=search.aspx%3Fq%3D*%26icount%3D10%26mindate%3D2015-10-12%26maxdate%3D2015-10-19&days=7&getText=false&relative=true&facet=true";
	private CountryService countrys;//=new CountryService(NewsSensorActivity.this);
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        countrys=new CountryService(this.getActivity());	
        
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		rootView=inflater.inflate(R.layout.tab06hotcountry, container, false);
		return rootView;
	//	return inflater.inflate(R.layout.tab01, container, false);
	}
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 super.onActivityCreated(savedInstanceState);		 
		 mListView6=(PullToRefreshListView)rootView.findViewById(R.id.pull_to_refresh_listview_hotcountry);
		 mListView6.setOnItemClickListener(this);  
		 ListItems = new ArrayList<Topic>(); 
		 mAdapter6=new SampleListAdapter(getActivity(),ListItems);
		// p=new HotTopicService(this.getActivity());		   
		 ShowNetUnuse.showpic(imagetoast,getActivity());
	     initProvince();
	        mAdapter6 = new SampleListAdapter(getActivity(),ListItems);  
	        // 设置PullToRefresh  
	    
	        mListView6.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.PULL_FROM_START);  
	        mListView6.setOnRefreshListener(new OnRefreshListener<ListView>(){  
	   
				public void onRefresh(PullToRefreshBase<ListView> refreshView) {
					// TODO Auto-generated method stub
	           				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),  
	                                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);  
	           			 refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
	                     refreshView.getLoadingLayoutProxy().setPullLabel("下拉刷新");
	                     refreshView.getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
	                     refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新时间:" + label);
	                      	
		                  new FinishRefresh().execute(); 
	                      
				}
	        });  
	       mListView6.setAdapter(mAdapter6);  
	      //设置自动刷新
	       if( countrys.getcount()==0)
	           mListView6.setRefreshing(true);
		
	}
	 private void initProvince() {
			// TODO Auto-generated method stub
			 ListItems = new ArrayList<Topic>();  
			
			 if(countrys.getcount()>0)
		        for (int i = 0; i <countrys.getcount(); i++) {  
		            ListItems.add(countrys.find(i));  
		        }
			 else
			 {
				 if(CheckNetState.checkNetwork(getActivity()))
				 {
					  ListItems.add(new Topic(0,"稍等...正在加载","",""));
					  
				 }
				 
				 else
					 ListItems.add(new Topic(0,"加载失败，检查网络连接。","",""));
				  
			 }
				   
		}
	 private List<Topic> getCountryData(String url) {
			// TODO Auto-generated method stub
	     List<Topic> list=new ArrayList<Topic>();
		 try {
			String jsonString=ReadStreamCommon.readStream(new URL(url).openStream());
			    JSONObject data=new JSONObject(jsonString);
				JSONObject d=data.getJSONObject("d");
				JSONArray facets=d.getJSONArray("Facets");
				JSONObject topics=facets.getJSONObject(6);
				//String k=topics.getString("Key");
				JSONObject Value=topics.getJSONObject("Value");
				JSONArray Items=Value.getJSONArray("Items");
				for(int i=0;i<Items.length();i++)//
				{
					JSONObject itemsobject=Items.getJSONObject(i);
					String key=itemsobject.getString("key").substring(7);
					String value=itemsobject.getString("value");					
					
					Topic topic=new Topic(i,key,value);
					  if(!countrys.findor(i))    countrys.save(topic);
					  else    countrys.update(topic);
					
					list.add(topic);
				//	HttpUtils.getNewsmsgJson(key, getNewsHandler2);
				}
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			return list;
		}
	 class FinishRefresh extends AsyncTask<Void,Void,List<Topic>>{

			


			@Override
			protected List<Topic> doInBackground(Void... params) {
				// TODO Auto-generated method stub
				 if(!CheckNetState.checkNetwork(getActivity()))
	             {
	                 //显示Toast
					 Toast.makeText(getActivity(), "网络连接不可用", Toast.LENGTH_SHORT).show();
	                 return null;
	             }
				 else return getCountryData(URL);
			}

			@Override
			protected void onPostExecute(List<Topic> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
	            if(result!=null){
					ListItems.clear();
				    for(int i=0;i<result.size();i++)
					 ListItems.add(result.get(i));
				}
				
	            mAdapter6.notifyDataSetChanged();  
	            mListView6.onRefreshComplete(); 
			}
	}
	    
	 
	 
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Topic topic=ListItems.get(arg2-1);
		Log.i("click", "1w");
		Intent intent=new Intent(getActivity(),BrowseActivity.class);
		intent.putExtra("name",topic.getName() );
	     
	     
	   	Log.i("click", "5w");
	  	startActivity(intent);
	}



	

}
