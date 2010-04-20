package jorus.patterns;

import jorus.array.Array2d;
import jorus.operations.bpo.BpoConvertByteDouble;
import jorus.parallel.PxSystem;

public class PatSet {

    private static final BpoConvertByteDouble conv = new BpoConvertByteDouble();
    
    public static Array2d dispatch(Array2d dst, Array2d src) {
     
        if (PxSystem.initialized()) {                           // run parallel
       
            final PxSystem px = PxSystem.get();
            final int rank = px.myCPU();
            
            try {
                if (src.getLocalState() != Array2d.VALID ||
                        src.getDistType() != Array2d.PARTIAL) {

                    if (rank == 0) System.out.println("BPO SCATTER 1...");
                    
                    px.scatter(src);
                }
                
                if (!dst.hasPartialData()) { 
               
                    byte [] tmp = (byte[]) src.getPartialDataReadOnly();
                    
                    dst.setPartialData(src.getPartialWidth(), 
                            src.getPartialHeight(), new double[tmp.length], 
                            Array2d.VALID, Array2d.PARTIAL);
                }
                
                conv.init(dst, src, true);
                conv.doIt((double [])dst.getPartialDataWriteOnly(), 
                        (byte [])src.getPartialDataReadWrite());

                dst.setGlobalState(Array2d.INVALID);
                
//              if (PxSystem.myCPU() == 0) System.out.println("BPO GATHER...");
//              PxSystem.gatherOFT(dst);

            } catch (Exception e) {
                System.err.println("Failed to perform operation!");
                e.printStackTrace(System.err);
            }

        } else {
            if (!dst.hasData()) {

                byte[] tmp = (byte[]) src.getDataReadOnly();

                dst.setData(src.getWidth(), src.getHeight(),
                        new double[tmp.length], Array2d.VALID);
            }
            
            BpoConvertByteDouble tmp = new BpoConvertByteDouble();
            
            tmp.init(dst, src, false);
            tmp.doIt((double [])dst.getDataWriteOnly(), 
                    (byte [])src.getDataReadWrite());

            dst.setGlobalState(Array2d.INVALID);
        }

        return dst;
    }

    
}
