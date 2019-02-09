package tamirmo.uncrowd.utilities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class NavigationActivityStarted {
    public static void startNavigationActivity(Context context, double latToNavigateTo, double lonToNavigateTo){
        Intent navIntent =
                new Intent(Intent.ACTION_VIEW);
        navIntent.setData(Uri.parse("geo:" + latToNavigateTo + "," + lonToNavigateTo ));
        context.startActivity(navIntent);
    }
}
