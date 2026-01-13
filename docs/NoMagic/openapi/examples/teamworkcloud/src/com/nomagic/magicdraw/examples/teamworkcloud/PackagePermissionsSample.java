/*
 * Copyright © 2018 NoMagic. Inc.
 * All Rights Reserved.
 */
package com.nomagic.magicdraw.examples.teamworkcloud;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.esi.persistence.security.PermissionException;
import com.nomagic.magicdraw.esi.persistence.security.PermissionService;
import com.nomagic.magicdraw.security.*;
import com.nomagic.magicdraw.utils.MDLog;
import com.nomagic.magicdraw.utils.StateChangeHandler;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.EList;

import javax.annotation.CheckForNull;

/**
 * <p>An example on how to use {@link PermissionService}. {@link PackagePermissionsSample This}
 * sample covers some of the most common use cases that are encountered when dealing with
 * package level permissions:
 * <ul>
 * <li>Creating a new instance of {@link PackageAccessPermission} and adding it
 * to a list in {@link PackagePermissions};</li>
 * <li>Reading {@link PackagePermissions} for a specific package;</li>
 * <li>Updating {@link PackageAccessPermission} by using convenience setters;</li>
 * <li>Deleting {@link PackageAccessPermission} for a specific package.</li>
 * </ul>
 * <p>To show <em>project modified</em> question in UI when closing a project without saving it,
 * the former should be marked as dirty by using {@code setDirty} method:
 * {@link Project#setDirty(boolean, StateChangeHandler.DirtyType)} with a
 * {@link com.nomagic.magicdraw.utils.StateChangeHandler.DirtyType DirtyType} value of
 * {@link com.nomagic.magicdraw.utils.StateChangeHandler.DirtyType#HARD_DIRTY HARD_DIRTY}:
 *
 * <pre>{@code
 * Package aPackage = ... // package whose permissions have been modified in any way (deleted/updated/added)
 *
 * Project.getProject(aPackage).getStateChangeHandler().setDirty(true, aPackage);
 * Project.getProject(aPackage).setDirty(true, DirtyType.HARD_DIRTY);
 * }</pre>
 * <p>However this operation is optional and not enforced by the {@link PermissionService API}.
 *
 * @author Sarunas Sarakojis
 * @see PermissionService
 */
@SuppressWarnings("unused")
public class PackagePermissionsSample
{
    /**
     * Methods creates a new instance of {@link PackageAccessPermission} and adds it
     * to {@link java.util.List list} that one of the methods of {@link PackagePermissions}
     * returns.
     *
     * @param pack      an instance of a {@link Package}
     * @param principal an instance of a {@link Principal}
     */
    public static void addNewPermission(Package pack, Principal principal)
    {
        try
        {
            PackageAccessPermission permission = SecurityFactory.eINSTANCE.createPackageAccessPermission();

            permission.setAction(Action.READ_WRITE);
            permission.setApplication(com.nomagic.magicdraw.security.Application.PACKAGE_AND_SUBPACKAGES);
            permission.setPrincipal(principal);

            getPackagePermissions(pack).getAccessPermissions().add(permission);
        }
        catch (PermissionException e)
        {
            MDLog.getGeneralLog().error("An error occurred while accessing package permissions", e);
        }
    }

    /**
     * Returns {@link PackagePermissions} for a specified {@link Package package}.
     *
     * @param pack an instance of a {@link Package}
     * @return an object that represents {@code pack}'s {@link PackagePermissions}
     * @throws PermissionException if the access of package permissions has failed
     */
    public static PackagePermissions getPackagePermissions(Package pack) throws PermissionException
    {
        return Project.getProject(pack).getPrimaryProject()
                .getServiceOrFail(PermissionService.class).getPermissions(pack);
    }

    /**
     * Method logs each permission of the specified {@link Package package} to the
     * {@link Logger general log}.
     *
     * @param pack an instance of a {@link Package}
     */
    public static void logPermissions(Package pack)
    {
        try
        {
            PackagePermissions permissions = getPackagePermissions(pack);

            MDLog.getGeneralLog().info(String.format("Permissions for package %s:", pack.getName()));
            for (PackageAccessPermission p : permissions.getAccessPermissions())
            {
                MDLog.getGeneralLog().info(String.format("[Principal: %s, Action: %s, Application: %s]",
                                                         p.getPrincipal().getName(), p.getAction(), p.getApplication()));
            }
        }
        catch (PermissionException e)
        {
            MDLog.getGeneralLog().error("An error occurred while accessing package permissions", e);
        }
    }

    /**
     * Method walks through each {@link PackageAccessPermission permission} of the specified
     * {@link Package package} and sets it's {@link Action} to {@link Action#READ_WRITE READ_WRITE}.
     *
     * @param pack an instance of a {@link Package}
     */
    public static void setPackagePermissionsToReadWrite(Package pack)
    {
        try
        {
            PackagePermissions permissions = getPackagePermissions(pack);

            for (PackageAccessPermission p : permissions.getAccessPermissions())
            {
                p.setAction(Action.READ_WRITE);
            }
        }
        catch (PermissionException e)
        {
            MDLog.getGeneralLog().error("An error occurred while accessing package permissions", e);
        }
    }

    /**
     * Method deletes {@link PackageAccessPermission permisison} of specified {@link Package package}
     * at the given {@code index}.
     *
     * @param pack  an instance of a {@link Package}
     * @param index an index of a permission that should be removed
     * @return the {@link PackageAccessPermission} previously at the specified {@code index}
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   {@code (index < 0 || index >= permissions.size()}
     */
    @CheckForNull
    public static PackageAccessPermission deletePackagePermission(Package pack, int index)
    {
        try
        {
            EList<PackageAccessPermission> permissions = getPackagePermissions(pack).getAccessPermissions();

            return permissions.remove(index);
        }
        catch (PermissionException e)
        {
            MDLog.getGeneralLog().error("An error occurred while accessing package permissions", e);
        }

        return null;
    }
}
