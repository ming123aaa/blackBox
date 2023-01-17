package android.os;

import android.os.Bundle;

/** @hide */
 interface IRemoteCallback {

    void sendResult(in Bundle data);
}
