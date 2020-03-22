package com.pubstate;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;
import java.io.IOException;

/**
 * Code from https://ebean.io/docs/db-migrations/
 * Intellij IDEA version must be ≥ 2019.3.3
 */
public class GenerateDbMigration {

  /**
   * Generate the next "DB schema DIFF" migration.
   */
  public static void main(String[] args) throws IOException {
    DbMigration dbMigration = DbMigration.create();
    dbMigration.setPlatform(Platform.MYSQL);

    dbMigration.generateMigration();
  }
}
