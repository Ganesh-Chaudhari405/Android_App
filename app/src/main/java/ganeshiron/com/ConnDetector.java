package ganeshiron.com;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnDetector {

    Context context;



    public ConnDetector(Context context) {
       this.context =context;
    }

    public boolean conn()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null)
        {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info !=null)
            {
                if(info.getState() == NetworkInfo.State.CONNECTED)
                {
                    return  true;
                }
            }
        }
        return  false;
    }
}
