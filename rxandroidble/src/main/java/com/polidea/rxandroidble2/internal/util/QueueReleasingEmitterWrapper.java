package com.polidea.rxandroidble2.internal.util;


import com.polidea.rxandroidble2.internal.QueueOperation;
import com.polidea.rxandroidble2.internal.serialization.QueueReleaseInterface;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Emitter;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;

/**
 * A convenience class to use in {@link QueueOperation} subclasses. It wraps the {@link Emitter}
 * and {@link QueueReleaseInterface} and makes sure that the {@link io.reactivex.disposables.Disposable} it was subscribed to will finish
 * and call {@link QueueReleaseInterface#release()} in either {@link #onComplete()} or {@link #onError(Throwable)} in case of the wrapped
 * emitter being unsubscribed / canceled.
 * @param <T> parameter of the wrapped {@link Emitter}
 */
public class QueueReleasingEmitterWrapper<T> implements Observer<T>, Cancellable {

    private final AtomicBoolean isEmitterCanceled = new AtomicBoolean(false);

    private final ObservableEmitter<T> emitter;

    private final QueueReleaseInterface queueReleaseInterface;

    public QueueReleasingEmitterWrapper(ObservableEmitter<T> emitter, QueueReleaseInterface queueReleaseInterface) {
        this.emitter = emitter;
        this.queueReleaseInterface = queueReleaseInterface;
        emitter.setCancellable(this);
    }

    @Override
    public void onComplete() {
        queueReleaseInterface.release();
        emitter.onComplete();
    }

    @Override
    public void onError(Throwable e) {
        queueReleaseInterface.release();
        emitter.tryOnError(e);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        emitter.onNext(t);
    }

    @Override
    synchronized public void cancel() {
        isEmitterCanceled.set(true);
    }

    synchronized public boolean isWrappedEmitterUnsubscribed() {
        return isEmitterCanceled.get();
    }
}
