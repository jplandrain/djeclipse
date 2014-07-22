package org.nidget.eclipse.djeclipse.decompilers.cfr;

import org.benf.cfr.reader.entities.ClassFile;
import org.benf.cfr.reader.entities.Method;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.state.TypeUsageCollector;
import org.benf.cfr.reader.util.CannotLoadClassException;
import org.benf.cfr.reader.util.ConfusedCFRException;
import org.benf.cfr.reader.util.getopt.BadParametersException;
import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.util.output.Dumper;
import org.benf.cfr.reader.util.output.ToStringDumper;
import org.eclipse.core.runtime.IPath;
import org.nidget.eclipse.djeclipse.decompilers.exceptions.DecompilerException;

public class CFRDecompiler {

	public static String decompile(IPath jarPath, IPath classPath) {
		String classPathStr = classPath.toString();
		GetOptParser getOptParser = new GetOptParser();

		try {
			Options options = (Options)getOptParser.parse(new String[] {classPathStr}, OptionsImpl.getFactory());		
			DCCommonState dcCommonState = new DCCommonState(options);
			Dumper dumper = new ToStringDumper();
			
			ClassFile c = dcCommonState.getClassFileMaybePath(options.getFileName());
			dcCommonState.configureWith(c);
			try {
				c = dcCommonState.getClassFile(c.getClassType());
			} catch (CannotLoadClassException e) {}
			c.loadInnerClasses(dcCommonState);
			c.analyseTop(dcCommonState);
			
			TypeUsageCollector collectingDumper = new TypeUsageCollector(c);
			c.collectTypeUsages(collectingDumper);
			
			String methname = options.getMethodName();
			if (methname == null) {
				c.dump(dumper);
			} else {
				try {
					for (Method method : c.getMethodByName(methname)) {
						method.dump(dumper, true);
					}
				} catch (NoSuchMethodException e) {
					throw new BadParametersException("No such method '" + methname + "'.", OptionsImpl.getFactory());
				}
			}
			return dumper.toString();
		} catch (CannotLoadClassException clce) {
			throw new DecompilerException(clce);
		} catch (BadParametersException bpe) {
			throw new DecompilerException(bpe); 
		} catch (ConfusedCFRException ccfre) {
			throw new DecompilerException(ccfre);
		} catch (RuntimeException rte) {
			throw new DecompilerException(rte);
		}
	}
}