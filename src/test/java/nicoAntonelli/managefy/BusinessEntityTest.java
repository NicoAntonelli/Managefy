package nicoAntonelli.managefy;

import jakarta.persistence.Transient;
import nicoAntonelli.managefy.entities.Business;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BusinessEntityTest {
    @Test
    void currentUserRoleShouldBeTransientStringRoleName() throws NoSuchFieldException {
        Field field = Business.class.getDeclaredField("currentUserRole");
        assertNotNull(field.getAnnotation(Transient.class));
        assertEquals(String.class, field.getType());
    }
}
