package com.dgsd.shifttracker.data;

import com.dgsd.shifttracker.data.DbContract.Field;
import com.dgsd.shifttracker.data.DbContract.Table;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class DbContractTest {

    @Test
    public void testTableCreateSql() {
        final Table table = new Table("test_table",
                new Field("a", Field.TYPE_INTEGER),
                new Field("b", Field.TYPE_INTEGER),
                new Field("c", Field.TYPE_INTEGER)
        );

        assertThat(table.getCreateSql()).isEqualTo(
                "CREATE TABLE IF NOT EXISTS test_table(a INTEGER ,b INTEGER ,c INTEGER )"
        );
    }

    @Test
    public void testColumnIndex() {
        final Field a = new Field("a", Field.TYPE_INTEGER);
        final Field b = new Field("b", Field.TYPE_INTEGER);
        final Field c = new Field("c", Field.TYPE_INTEGER);

        final Table table = new Table("test_table", a, b, c);

        assertThat(table.columnIndex(a)).isEqualTo(0);
        assertThat(table.columnIndex(b)).isEqualTo(1);
        assertThat(table.columnIndex(c)).isEqualTo(2);
    }

    @Test(expected = IllegalStateException.class)
    public void testColumnIndexWhenDoesntExist() {
        final Field a = new Field("a", Field.TYPE_INTEGER);
        final Table table = new Table("test_table", a);

        assertThat(table.columnIndex(new Field("b", Field.TYPE_INTEGER))).isEqualTo(2);
    }
}