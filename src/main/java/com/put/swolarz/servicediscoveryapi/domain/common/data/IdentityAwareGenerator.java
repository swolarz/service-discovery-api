package com.put.swolarz.servicediscoveryapi.domain.common.data;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.io.Serializable;

public class IdentityAwareGenerator extends SequenceStyleGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        if (object instanceof BaseEntity) {
            BaseEntity entity = (BaseEntity) object;

            if (entity.getId() != null)
                return entity.getId();
        }
//        Serializable id = session.getEntityPersister(null, object).getClassMetadata().getIdentifier(object, session);
//        return id != null ? id : super.generate(session, object);

        return super.generate(session, object);
    }
}
