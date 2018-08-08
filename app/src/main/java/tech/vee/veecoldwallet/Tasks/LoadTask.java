package tech.vee.veecoldwallet.Tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import tech.vee.veecoldwallet.Util.FileUtil;

public class LoadTask extends AsyncTask<Void, Void, Void> {
    private ProgressDialog dialog;
    private Activity activity;
    private String password;
    private String path;
    private String seed;

    public LoadTask(Activity activity, String password, String path) {
        dialog = new ProgressDialog(activity);
        this.activity = activity;
        this.password = password;
        this.path = path;
    }

    public String getSeed() {
        return seed;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Loading ...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        seed = FileUtil.load(password, path);
        return null;
    }

}
