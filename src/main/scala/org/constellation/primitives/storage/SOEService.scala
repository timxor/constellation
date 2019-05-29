package org.constellation.primitives.storage

import org.constellation.primitives.Schema.SignedObservationEdgeCache

class SOEService() extends StorageService[SignedObservationEdgeCache](Some(1440))
