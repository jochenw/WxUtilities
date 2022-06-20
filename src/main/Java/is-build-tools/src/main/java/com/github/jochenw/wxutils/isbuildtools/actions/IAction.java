package com.github.jochenw.wxutils.isbuildtools.actions;

public interface IAction<O> {
	public void run(O pOptions) throws Exception;
}
