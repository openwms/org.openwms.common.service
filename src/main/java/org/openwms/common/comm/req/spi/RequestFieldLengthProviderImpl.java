/*
 * openwms.org, the Open Warehouse Management System.
 * Copyright (C) 2014 Heiko Scherrer
 *
 * This file is part of openwms.org.
 *
 * openwms.org is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * openwms.org is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openwms.common.comm.req.spi;

import org.openwms.common.location.LocationPK;
import org.openwms.common.transport.Barcode;
import org.springframework.stereotype.Component;

/**
 * A RequestFieldLengthProvider.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
@Component
class RequestFieldLengthProviderImpl implements RequestFieldLengthProvider{

    @Override
    public int barcodeLength() {
        return Barcode.BARCODE_LENGTH;
    }

    @Override
    public int locationIdLength() {
        return LocationPK.PK_LENGTH;
    }

    @Override
    public int noLocationIdFields() {
        return LocationPK.NUMBER_OF_KEYS;
    }
}
