#!/bin/sh
"/Applications/Android Studio.app/Contents/jbr/Contents/Home/bin/java" -cp "/Applications/Android Studio.app/Contents/plugins/vcs-git/lib/git4idea-rt.jar:/Applications/Android Studio.app/Contents/lib/externalProcess-rt.jar" git4idea.editor.GitRebaseEditorApp "$@"
