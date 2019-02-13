
//////////////////////////////////////////////////////////////////////////////
//This file is part of Cireson Barcode Scanner. 
//
//Cireson Barcode Scanner is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Cireson Barcode Scanner is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with Cireson Barcode Scanner.  If not, see<https://www.gnu.org/licenses/>.
/////////////////////////////////////////////////////////////////////////////


package com.cireson.assetmanager.assistance;

import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.util.CiresonConstants;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by welcome on 8/12/2014.
 */
public class Editor {

    /*Instances..*/
    GlobalData globalData;

    public Editor(){
        globalData = GlobalData.getInstance();
    }

    public void setAssetsProperties(Assets asset){
        ArrayList<Assets> temporaryListOfAssets = new ArrayList<Assets>();
        temporaryListOfAssets.add(asset);
        if(GlobalData.ACTIVE_MAIN_MENU==CiresonConstants.SWAP_ASSETS){
            if(globalData.getSwaper().getSwap()){
                setAssetPropertiesForSwapAssets1(temporaryListOfAssets);
            }else{
                setAssetPropertiesForSwapAssets2(temporaryListOfAssets);
            }

        }
    }

    public void setAssetPropertiesForReceiveAssets(ArrayList<Assets> targetAssets){
        setCommonAssetProperties(targetAssets);
        setDate(CiresonConstants.RECEIVED_ASSETS,targetAssets);
    }

    public void setAssetPropertiesForAddAssets(ArrayList<Assets> targetAssets){
        Assets sourceAsset = globalData.getTemporaryAsset();
        for(Assets asset:targetAssets){
            if(sourceAsset.manufacturer!=null){
                asset.setManufacturer(sourceAsset.manufacturer);
            }
            if(sourceAsset.model!=null){
                asset.setModel(sourceAsset.model);
            }
            /*Other essential properties to add new assets..*/
            asset.setClassTypeId(CiresonConstants.HardwareAsset_Type);
            asset.setHardwareAssetId(UUID.randomUUID().toString());
            asset.setNameRelationship();
        }
        setCommonAssetProperties(targetAssets);
        setDate(CiresonConstants.RECEIVED_ASSETS,targetAssets);
    }

    public void setAssetPropertiesForSwapAssets1(ArrayList<Assets> targetAssets){
        setCommonAssetProperties(targetAssets);
        setDate(CiresonConstants.SWAP_ASSETS, targetAssets);
        Assets sourceAsset = globalData.getTemporaryAsset();
        if(sourceAsset.notes!=null){
            globalData.getSwaper().setNoteForFirstAsset(sourceAsset.notes + "\n\n");
        }
    }

    public void setAssetPropertiesForSwapAssets2(ArrayList<Assets> targetAssets){
        setCommonAssetProperties(targetAssets);
        setDate(CiresonConstants.SWAP_ASSETS,targetAssets);
        Assets sourceAsset = globalData.getTemporaryAsset();
        if(sourceAsset.notes!=null){
            globalData.getSwaper().setNoteForSecondAsset(sourceAsset.notes + "\n\n");
        }
    }

    public void setAssetPropertiesForEditAssets(ArrayList<Assets> targetAssets){
        setCommonAssetProperties(targetAssets);
        setDate(CiresonConstants.EDIT_ASSETS,targetAssets);
    }

    public void setCommonAssetProperties(ArrayList<Assets> targetAssets){
        Assets sourceAsset = globalData.getTemporaryAsset();
        for(Assets asset:targetAssets){
            if(sourceAsset.hardwareAssetStatus.id!=null){
                asset.setHardwareStatusId(sourceAsset.hardwareAssetStatus.id);
            }
            if(sourceAsset.hardwareAssetStatus.name!=null){
                asset.setHardwareStatusName(sourceAsset.hardwareAssetStatus.name);
            }
            if(sourceAsset.targetHardwareAssetHasLocation.baseId!=null){
                asset.setLocationBaseId(sourceAsset.targetHardwareAssetHasLocation.baseId);
            }
            if(sourceAsset.targetHardwareAssetHasLocation.classTypeId!=null){
                asset.setLocationClassTypeId(sourceAsset.targetHardwareAssetHasLocation.classTypeId);
            }
            if(sourceAsset.targetHardwareAssetHasLocation.displayName!=null){
                asset.setLocationDisplayName(sourceAsset.targetHardwareAssetHasLocation.displayName);
            }
            if(sourceAsset.targetHardwareAssetHasOrganization.baseId!=null){
                asset.setOrganizationBaseId(sourceAsset.targetHardwareAssetHasOrganization.baseId);
            }
            if(sourceAsset.targetHardwareAssetHasOrganization.classTypeId!=null){
                asset.setOrganizationClassTypeId(sourceAsset.targetHardwareAssetHasOrganization.classTypeId);
            }
            if(sourceAsset.targetHardwareAssetHasOrganization.displayName!=null){
                asset.setOrganizationDisplayName(sourceAsset.targetHardwareAssetHasOrganization.displayName);
            }
            if(sourceAsset.targetHardwareAssetHasCostCenter.baseId!=null){
                asset.setCostCenterBaseId(sourceAsset.targetHardwareAssetHasCostCenter.baseId);
            }
            if(sourceAsset.targetHardwareAssetHasCostCenter.classTypeId!=null){
                asset.setCostCenterClassTypeId(sourceAsset.targetHardwareAssetHasCostCenter.classTypeId);
            }
            if(sourceAsset.targetHardwareAssetHasCostCenter.displayName!=null){
                asset.setCostCenterDisplayName(sourceAsset.targetHardwareAssetHasCostCenter.displayName);
            }
            if(sourceAsset.targetHardwareAssetHasPrimaryUser.baseId!=null){
                asset.setPrimaryUserBaseId(sourceAsset.targetHardwareAssetHasPrimaryUser.baseId);
            }
            if(sourceAsset.targetHardwareAssetHasPrimaryUser.classTypeId!=null){
                asset.setPrimaryUsersClassTypeId(sourceAsset.targetHardwareAssetHasPrimaryUser.classTypeId);
            }
            if(sourceAsset.targetHardwareAssetHasPrimaryUser.displayName!=null){
                asset.setPrimaryUserDisplayName(sourceAsset.targetHardwareAssetHasPrimaryUser.displayName);
            }
            if(sourceAsset.owndedBy.baseId!=null){
                asset.setCustodianBaseId(sourceAsset.owndedBy.baseId);
            }
            if(sourceAsset.owndedBy.classTypeId!=null){
                asset.setCustodianUsersClassTypeId(sourceAsset.owndedBy.classTypeId);
            }
            if(sourceAsset.owndedBy.userName!=null){
                asset.setCustodianUserName(sourceAsset.owndedBy.userName);
            }
        }
    }

    public void setNotes(ArrayList<Assets> targetAssets){
        /*Finally set notes for both assets..*/
        if(targetAssets!=null){
            if(targetAssets.size()>1){
                String savedNoteForFirstAsset;
                try {
                    savedNoteForFirstAsset = globalData.getSwaper().getNoteToAppendForFirstAsset().toString();
                }
                catch(NullPointerException ex){
                    savedNoteForFirstAsset = "";
                }
                String originalNoteOfFirst = targetAssets.get(0).notes;
                if(originalNoteOfFirst!=null){
                    if(savedNoteForFirstAsset!=null&&!savedNoteForFirstAsset.isEmpty()){
                        String concatinatedString = originalNoteOfFirst.toString().concat(savedNoteForFirstAsset.toString());
                        targetAssets.get(0).notes = concatinatedString;
                        targetAssets.get(0).setNotes(concatinatedString);
                    }
                }else{
                    if(savedNoteForFirstAsset!=null&&!savedNoteForFirstAsset.isEmpty()){
                        targetAssets.get(0).notes = savedNoteForFirstAsset;
                        targetAssets.get(0).setNotes(savedNoteForFirstAsset);
                    }
                }

                String savedNoteForSecondAsset;

                try {
                    savedNoteForSecondAsset = globalData.getSwaper().getNoteToAppendForSecondAsset().toString();
                }
                catch(NullPointerException ex){
                    savedNoteForSecondAsset = "";
                }

                String originalNoteOfSecond = targetAssets.get(1).notes;
                if(originalNoteOfSecond!=null){
                    if(savedNoteForSecondAsset!=null&&!savedNoteForSecondAsset.isEmpty()){
                        String concatinatedString = originalNoteOfSecond.toString().concat(savedNoteForSecondAsset.toString());
                        targetAssets.get(1).notes = concatinatedString;
                        targetAssets.get(1).setNotes(concatinatedString);
                    }
                }else{
                    if(savedNoteForSecondAsset!=null&&!savedNoteForSecondAsset.isEmpty()){
                        targetAssets.get(1).notes = savedNoteForSecondAsset;
                        targetAssets.get(1).setNotes(savedNoteForSecondAsset);
                    }
                }
            }
        }
    }

    public void setDate(int which,ArrayList<Assets> targetAssets){
        Assets sourceAsset = globalData.getTemporaryAsset();
        for(Assets asset:targetAssets){
            switch(which){
                case CiresonConstants.RECEIVED_ASSETS:
                case CiresonConstants.EDIT_ASSETS:
                    if(sourceAsset.receivedDate!=null){
                        asset.setReceivedDate(sourceAsset.receivedDate);
                    }
                    break;
                case CiresonConstants.SWAP_ASSETS:
                    if(globalData.getSwaper().getSwap()){
                        if(sourceAsset.loanReturnedDate!=null){
                            asset.setLoanReturnedDate(sourceAsset.loanReturnedDate);
                        }
                    }else{
                        if(sourceAsset.loanedDate!=null){
                            asset.setLoanedDate(sourceAsset.loanedDate);
                        }
                    }
                    break;
                case CiresonConstants.DISPOSE_ASSETS:
                    if(sourceAsset.disposalDate!=null){
                        asset.setDisposalDate(sourceAsset.disposalDate);
                    }
                    break;
            }
        }
    }

}
