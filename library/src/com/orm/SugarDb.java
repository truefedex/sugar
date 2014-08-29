package com.orm;

import static com.orm.util.ManifestHelper.getDatabaseVersion;
import static com.orm.util.ManifestHelper.getDebugEnabled;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orm.util.FileUtil;
import com.orm.util.ManifestHelper;
import com.orm.util.SugarCursorFactory;

public class SugarDb extends SQLiteOpenHelper {

    private final SchemaGenerator schemaGenerator;
    private SQLiteDatabase sqLiteDatabase;
    private boolean usedPreinstalledDatabase = false;

    public SugarDb(Context context) {
        super(context, ManifestHelper.getDatabaseName(context),
                new SugarCursorFactory(getDebugEnabled(context)), getDatabaseVersion(context));
        schemaGenerator = new SchemaGenerator(context);
        
        String preinstalledDatabaseFileName = ManifestHelper.getPreinstalledDatabaseName(context);
        if (preinstalledDatabaseFileName != null) {
        	usedPreinstalledDatabase = true;
        	unpackPreinstalledDatabase(context, preinstalledDatabaseFileName);
        }
    }

	@Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
		if (usedPreinstalledDatabase) {
			return;
		}
        schemaGenerator.createDatabase(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        schemaGenerator.doUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }

    public synchronized SQLiteDatabase getDB() {
        if (this.sqLiteDatabase == null) {
            this.sqLiteDatabase = getWritableDatabase();
        }

        return this.sqLiteDatabase;
    }

	private void unpackPreinstalledDatabase(Context context, String preinstalledDatabaseFileName) {
    	try {
	        File localDatabaseFile = context.getDatabasePath(ManifestHelper.getDatabaseName(context));
	        if (!localDatabaseFile.exists()) {
	        	getWritableDatabase();//needed for initialize internal structures of SQLiteOpenHelper
	        	FileUtil.writeStreamToFile(context.getAssets().open(preinstalledDatabaseFileName), localDatabaseFile);
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
    		usedPreinstalledDatabase = false;
    	}
	}
}
